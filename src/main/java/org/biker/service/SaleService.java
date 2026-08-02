package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.dto.*;
import org.biker.model.*;
import org.biker.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    // Inyección de repositorios necesarios
    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ClientRepository clientRepository;
    private final SparePartRepository sparePartRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AccountingEntryService accountingEntryService;

    /**
     * Registrar una nueva venta por ventanilla
     */
    public SaleResponse registerSale(SaleRequest request) {
        // Buscar cliente si viene en la petición (puede ser null)
        Client client;
        if (request.clientId() != null) {
            client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        } else {
            // Crear cliente nuevo si no existe
            client = new Client();
            client.setClientName(request.customerName()); // 👈 asegúrate que SaleRequest tenga este campo
            client.setDocumento(request.documento());
            client = clientRepository.save(client);
        }


        // Buscar método de pago (obligatorio)
        PaymentMethod method = paymentMethodRepository.findById(request.paymentMethodId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // Crear la venta principal
        Sale sale = new Sale();
        sale.setSaleNumber("SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()); // Genera número único
        sale.setSaleDate(LocalDateTime.now()); // Fecha actual
        sale.setClient(client); // Cliente (puede ser null)
        sale.setPaymentMethod(method); // Método de pago
        sale.setTotal(BigDecimal.ZERO); // Inicialmente en cero

        // Lista para acumular los detalles
        List<SaleDetail> details = new ArrayList<>();
        List<SaleItemResponse> itemResponses = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        // Recorrer cada producto enviado en la petición
        for (SaleItemRequest itemReq : request.items()) {
            SparePart sparePart = sparePartRepository.findById(itemReq.sparePartId())
                    .orElseThrow(() -> new RuntimeException("Repuesto no encontrado"));

            // Validar stock
            if (sparePart.getStock() < itemReq.quantity()) {
                throw new RuntimeException("Stock insuficiente para " + sparePart.getName());
            }

            // Validar precio (no puede ser menor al precio de compra)
            if (itemReq.unitPrice().compareTo(sparePart.getPriceBuy()) < 0) {
                throw new RuntimeException("El precio de venta no puede ser menor al costo de compra ("
                        + sparePart.getPriceBuy() + ")");
            }

            // Descontar stock
            sparePart.setStock(sparePart.getStock() - itemReq.quantity());

            // Calcular subtotal
            BigDecimal subtotal = itemReq.unitPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            total = total.add(subtotal);

            // Crear detalle de venta
            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setSparePart(sparePart);
            detail.setQuantity(itemReq.quantity());
            detail.setUnitPrice(itemReq.unitPrice());
            detail.setSubtotal(subtotal);

            details.add(detail);

            // Crear respuesta para frontend
            itemResponses.add(new SaleItemResponse(
                    sparePart.getName(),
                    itemReq.quantity(),
                    itemReq.unitPrice(),
                    subtotal
            ));
        }

        // Asignar total final a la venta
        sale.setTotal(total);

        // Guardar venta y detalles
        Sale savedSale = saleRepository.save(sale);
        details.forEach(saleDetailRepository::save);

        AccountingEntryRequest entryDto = new AccountingEntryRequest(
                "INGRESO",
                savedSale.getTotal(),
                LocalDateTime.now(),
                "Venta #" + savedSale.getSaleNumber(),
                null,                  // payment
                savedSale.getId(),     // sale
                1L                   // expenseCategory
        );

        accountingEntryService.createEntry(entryDto);


        // Retornar respuesta completa
        return new SaleResponse(
                savedSale.getId(),
                savedSale.getSaleNumber(),
                savedSale.getSaleDate(),
                client != null ? client.getClientName() : "Venta sin cliente",
                method.getName(),
                total,
                itemResponses
        );
    }

    /**
     * Consultar todas las ventas
     */
    public List<SaleResponse> findAll() {
        return saleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Consultar ventas por cliente
     */
    public List<SaleResponse> findByClient(Long clientId) {
        return saleRepository.findByClientId(clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Dentro de SaleService
    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return toResponse(sale); // aquí sí puedes usar el private
    }


    /**
     * Convertir entidad Sale a SaleResponse
     */
    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> items = sale.getDetails()
                .stream()
                .map(d -> new SaleItemResponse(
                        d.getSparePart().getName(),
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getSubtotal()
                ))
                .toList();

        return new SaleResponse(
                sale.getId(),
                sale.getSaleNumber(),
                sale.getSaleDate(),
                sale.getClient() != null ? sale.getClient().getClientName() : "Venta sin cliente",
                sale.getPaymentMethod().getName(),
                sale.getTotal(),
                items
        );
    }
}

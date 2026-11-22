package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.AccountingEntryRequest;
import org.biker.dto.PaymentRequest;
import org.biker.dto.PaymentResponse;
import org.biker.model.*;
import org.biker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final WorkOrderRepository workOrderRepository;
    private final SparePartRepository sparePartRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final StatusRepository statusGeneralRepository;
    private final WorkOrderPaymentStatusRepository statusRepository;
    private final AccountingEntryService accountingEntryService;
    private final MovementCategoryRepository movementCategoryRepository;

    //
    public record SaleItemRequest(Long sparePartId, int quantity, double unitPrice) {}
    public record SaleRequest(Long clientId, Long paymentMethodId, List<SaleItemRequest> items) {}

    public record SaleItemResponse(String sparePartName, int quantity, double unitPrice, BigDecimal subtotal) {}
    public record SaleResponse(Long id, LocalDateTime saleDate, String clientName,
                               String paymentMethod, BigDecimal total, List<SaleItemResponse> items) {}


    // Registrar un pago (venta directa o abono de orden)
    @Transactional
    public PaymentResponse registerPayment(PaymentRequest request) {
        if (request.amount() == null || request.amount().signum() <= 0) {
            throw new IllegalArgumentException("El valor del pago debe ser mayor que cero");
        }
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        PaymentMethod method = paymentMethodRepository.findById(request.paymentMethodId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        WorkOrder workOrder = null;
        if (request.workOrderId() != null) {
            workOrder = workOrderRepository.findByIdForUpdate(request.workOrderId())
                    .orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada"));
        }

        if (workOrder != null) {
            if (workOrder.getStatus() == null || !"Pendiente Pago".equalsIgnoreCase(workOrder.getStatus().getName())) {
                throw new IllegalStateException("La orden no se encuentra en estado Pendiente Pago");
            }
            BigDecimal total = workOrder.getTotalValue() == null ? BigDecimal.ZERO : workOrder.getTotalValue();
            BigDecimal paid = paymentRepository.findByWorkOrderId(workOrder.getId()).stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal remaining = total.subtract(paid);
            if (remaining.signum() <= 0 || request.amount().compareTo(remaining) > 0) {
                throw new IllegalArgumentException("El pago excede el saldo pendiente de la orden: " + remaining.max(BigDecimal.ZERO));
            }
            if (!workOrder.getClient().getId().equals(client.getId())) {
                throw new IllegalArgumentException("El cliente no corresponde a la orden de trabajo");
            }
        }

        SparePart sparePart = null;
        if (request.sparePartId() != null) {
            sparePart = sparePartRepository.findById(request.sparePartId())
                    .orElseThrow(() -> new RuntimeException("Repuesto no encontrado"));
        }

        Payment payment = new Payment(
                request.amount(),
                LocalDateTime.now(),
                client,
                workOrder,
                sparePart,
                method
        );

        Payment saved = paymentRepository.save(payment);

        if (workOrder != null) {
            Long serviceCategoryId = movementCategoryRepository.findByCode("SERVICIOS_TALLER")
                    .orElseThrow(() -> new IllegalStateException("Falta categoría contable SERVICIOS_TALLER"))
                    .getId();
            AccountingEntryRequest entryDto = new AccountingEntryRequest(
                    "INGRESO", saved.getAmount(), LocalDateTime.now(),
                    "Abono a la orden de trabajo #" + workOrder.getOrderNumber(),
                    saved.getId(), null, serviceCategoryId
            );
            accountingEntryService.createEntry(entryDto);
        }

        // Actualizar estado de la orden si aplica
        if (workOrder != null) {
            updateWorkOrderPaymentStatus(workOrder);
        }

        return new PaymentResponse(
                saved.getId(),
                saved.getAmount(),
                saved.getPaymentDate(),
                client.getClientName(),
                workOrder != null ? workOrder.getOrderNumber() : null,
                sparePart != null ? sparePart.getName() : null,
                method.getName(),
                workOrder != null && workOrder.getPaymentStatus() != null
                        ? workOrder.getPaymentStatus().getStatus()   // ✅ aquí usar getStatus()
                        : null,
                workOrder != null ? workOrder.getId() : null   // 👈 aquí
        );



    }

    // Histórico de pagos por orden
    public List<PaymentResponse> getPaymentsByWorkOrder(Long workOrderId) {
        return paymentRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getAmount(),
                        p.getPaymentDate(),
                        p.getClient().getClientName(),
                        p.getWorkOrder() != null ? p.getWorkOrder().getOrderNumber() : null,
                        p.getSparePart() != null ? p.getSparePart().getName() : null,
                        p.getPaymentMethod().getName(),
                        p.getWorkOrder() != null && p.getWorkOrder().getPaymentStatus() != null
                                ? p.getWorkOrder().getPaymentStatus().getStatus()   // ✅ aquí también
                                : null,
                        p.getWorkOrder() != null ? p.getWorkOrder().getId() : null   // 👈 aquí
                ))

                .toList();
    }


    // Histórico de pagos por cliente
    public List<PaymentResponse> getPaymentsByClient(Long clientId) {
        return paymentRepository.findByClientId(clientId)
                .stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getAmount(),
                        p.getPaymentDate(),
                        p.getClient().getClientName(),
                        p.getWorkOrder() != null ? p.getWorkOrder().getOrderNumber() : null,
                        p.getSparePart() != null ? p.getSparePart().getName() : null,
                        p.getPaymentMethod().getName(),
                        p.getWorkOrder() != null && p.getWorkOrder().getPaymentStatus() != null
                                ? p.getWorkOrder().getPaymentStatus().getStatus()   // ✅ corregir aquí
                                : null,
                        p.getWorkOrder() != null ? p.getWorkOrder().getId() : null   // 👈 aquí
                ))

                .toList();
    }

    // Lógica para actualizar estado de pago de la orden

    private void updateWorkOrderPaymentStatus(WorkOrder workOrder) {
        BigDecimal totalPagado = paymentRepository.findByWorkOrderId(workOrder.getId())
                .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOrden = workOrder.getTotalValue();

        WorkOrderPaymentStatus status;
        if (totalPagado.compareTo(totalOrden) >= 0) {
            status = statusRepository.findFirstByStatusIgnoreCase("Pagado").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Pagado"));
            // 👇 además de actualizar el estado de pago, cambia el estado general
            Status finalizado = statusGeneralRepository.findByName("Finalizado")
                    .orElseThrow(() -> new RuntimeException("Estado 'Finalizado' no encontrado"));
            workOrder.setStatus(finalizado);
        } else if (totalPagado.compareTo(totalOrden) < 0 && totalPagado.signum() > 0) {
            status = statusRepository.findFirstByStatusIgnoreCase("Abonado").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Abonado"));
        } else {
            status = statusRepository.findFirstByStatusIgnoreCase("Pendiente").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Pendiente"));
        }

        workOrder.setPaymentStatus(status);
        workOrderRepository.save(workOrder);
    }

    //Trae todas la ventas
    public List<PaymentResponse> getAllSales() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //trae las ventas del dia
    public List<PaymentResponse> getSalesByDay(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return paymentRepository.findByPaymentDateBetween(start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //trae las ventas de la semana
    public List<PaymentResponse> getSalesByWeek(int year, int week) {
        LocalDate start = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), week)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        LocalDate end = start.plusWeeks(1);
        return paymentRepository.findByPaymentDateBetween(start.atStartOfDay(), end.atStartOfDay())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //trae las ventas del mes
    public List<PaymentResponse> getSalesByMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        return paymentRepository.findByPaymentDateBetween(start.atStartOfDay(), end.atStartOfDay())
                .stream()
                .map(this::toResponse)
                .toList();
    }



    // Registrar venta libre (ventanilla)
    public SaleResponse registerSale(SaleRequest request) {
        Client client = null;

        // Si viene clientId, se busca; si no, se permite venta sin cliente
        if (request.clientId() != null) {
            client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        }

        PaymentMethod method = paymentMethodRepository.findById(request.paymentMethodId())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        BigDecimal total = BigDecimal.ZERO;
        List<SaleItemResponse> itemResponses = new java.util.ArrayList<>();

        for (SaleItemRequest itemReq : request.items()) {
            SparePart sparePart = sparePartRepository.findById(itemReq.sparePartId())
                    .orElseThrow(() -> new RuntimeException("Repuesto no encontrado"));

            if (sparePart.getStock() < itemReq.quantity()) {
                throw new RuntimeException("Stock insuficiente para " + sparePart.getName());
            }

            if (itemReq.unitPrice() < sparePart.getPriceBuy().doubleValue()) {
                throw new RuntimeException("El precio de venta no puede ser menor al costo de compra ("
                        + sparePart.getPriceBuy() + ")");
            }

            sparePart.setStock(sparePart.getStock() - itemReq.quantity());

            BigDecimal subtotal = BigDecimal.valueOf(itemReq.unitPrice())
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            total = total.add(subtotal);

            itemResponses.add(new SaleItemResponse(
                    sparePart.getName(),
                    itemReq.quantity(),
                    itemReq.unitPrice(),
                    subtotal
            ));
        }

        Payment payment = new Payment(
                total,
                LocalDateTime.now(),
                client,
                null,
                null,
                method
        );

        Payment saved = paymentRepository.save(payment);

        return new SaleResponse(
                saved.getId(),
                saved.getPaymentDate(),
                client != null ? client.getClientName() : "Venta sin cliente", // ✅ fallback
                method.getName(),
                total,
                itemResponses
        );
    }

    public PaymentResponse findById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getClient().getClientName(),
                payment.getWorkOrder() != null ? payment.getWorkOrder().getOrderNumber() : null,
                payment.getSparePart() != null ? payment.getSparePart().getName() : null,
                payment.getPaymentMethod().getName(),
                payment.getWorkOrder() != null && payment.getWorkOrder().getPaymentStatus() != null
                        ? payment.getWorkOrder().getPaymentStatus().getStatus()
                        : null,
                payment.getWorkOrder() != null ? payment.getWorkOrder().getId() : null   // 👈 nuevo campo
        );
    }


    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getAmount(),
                p.getPaymentDate(),
                p.getClient().getClientName(),
                p.getWorkOrder() != null ? p.getWorkOrder().getOrderNumber() : null,
                p.getSparePart() != null ? p.getSparePart().getName() : null,
                p.getPaymentMethod().getName(),
                p.getWorkOrder() != null && p.getWorkOrder().getPaymentStatus() != null
                        ? p.getWorkOrder().getPaymentStatus().getStatus()   // ✅ aquí también
                        : null,
                p.getWorkOrder() != null ? p.getWorkOrder().getId() : null   // 👈 nuevo campo
        );

    }


}

package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.dto.AddSparePartRequest;
import org.biker.dto.WorkOrderDetailResponse;
import org.biker.dto.WorkOrderRequest;
import org.biker.dto.WorkOrderResponse;
import org.biker.model.*;
import org.biker.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.biker.dto.WorkOrderResponse.toResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final BrandRepository brandRepository;
    private final StatusRepository statusRepository;
    private final SparePartRepository sparePartRepository;
    private final WorkOrderDetailRepository workOrderDetailRepository;
    private final ClientRepository clientRepository;

    public WorkOrder create(WorkOrderRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Status status = statusRepository.findByName("En Progreso")
                .orElseThrow(() -> new RuntimeException("Status not found"));

        // Buscar cliente
        Client client;
        if (request.clientId() != null) {
            client = clientRepository.findById(request.clientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        } else {
            // Crear cliente nuevo si no existe
            client = new Client();
            client.setClientName(request.customerName());
            client.setDocumento(request.documento());
            client = clientRepository.save(client);
        }

        WorkOrder workOrder = new WorkOrder();
        workOrder.setClient(client);

        workOrder.setCustomerName(request.customerName());
        workOrder.setMotorcyclePlate(request.motorcyclePlate());
        workOrder.setModel(request.model());
        workOrder.setBrand(brand);
        workOrder.setStatus(status); // 🔥 automático
        workOrder.setLabour(BigDecimal.ZERO);
        workOrder.setTotalValue(BigDecimal.ZERO);
        workOrder.setCreatedAt(LocalDateTime.now());
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrder.setOrderNumber("WO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());


        /*
         * OBSERVATIONS
         */
        if (request.observations() != null) {

            List<Observation> observations = request.observations()
                    .stream()
                    .map(o -> {
                        Observation obs = new Observation();
                        obs.setDescription(o.description());
                        obs.setWorkOrder(workOrder);
                        return obs;
                    })
                    .toList();

            workOrder.setObservations(observations);
        }

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrderDetailResponse addDetail(Long workOrderId, AddSparePartRequest request) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (!workOrder.getStatus().getName().equals("En Progreso")) {
            throw new RuntimeException("Work order is not in progress");
        }

        SparePart sparePart = sparePartRepository.findById(request.sparePartId())
                .orElseThrow(() -> new RuntimeException("Spare part not found"));

        if (sparePart.getStock() < request.quantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        sparePart.setStock(sparePart.getStock() - request.quantity());

        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setWorkOrder(workOrder);
        detail.setSparePart(sparePart);
        detail.setQuantity(request.quantity());
        detail.setUnitPrice(sparePart.getPriceSell());

        workOrder.getDetails().add(detail);

        // recalcular total
        BigDecimal subtotal = sparePart.getPriceSell()
                .multiply(BigDecimal.valueOf(request.quantity()));

        workOrder.setTotalValue(
                workOrder.getTotalValue().add(subtotal)
        );

        workOrder.setUpdatedAt(LocalDateTime.now());

        return new WorkOrderDetailResponse(
                detail.getId(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getWorkOrder().getId(),
                detail.getSparePart().getId(),
                detail.getSparePart().getName()
        );
    }



    @Transactional
    public void returnSparePart(Long detailId) {

        WorkOrderDetail detail = workOrderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Detail not found"));

        WorkOrder workOrder = detail.getWorkOrder();

        if (!workOrder.getStatus().getName().equals("En Progreso")) {
            throw new RuntimeException("Only work orders in progress can be modified");
        }

        SparePart sparePart = detail.getSparePart();

        // devolver inventario
        sparePart.setStock(
                sparePart.getStock() + detail.getQuantity()
        );

        // calcular subtotal del detalle
        BigDecimal subtotal = detail.getUnitPrice()
                .multiply(BigDecimal.valueOf(detail.getQuantity()));

        // descontar del total de la orden
        workOrder.setTotalValue(
                workOrder.getTotalValue().subtract(subtotal)
        );

        workOrder.setUpdatedAt(LocalDateTime.now());

        // eliminar detalle
        workOrder.getDetails().remove(detail);

        workOrderDetailRepository.delete(detail);
    }
    @Transactional
    public void returnSparePartSpecific(
            Long detailId,
            Integer quantityToReturn) {

        WorkOrderDetail detail = workOrderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Detail not found"));

        WorkOrder workOrder = detail.getWorkOrder();

        if (!workOrder.getStatus().getName().equals("En Progreso")) {
            throw new RuntimeException("Only work orders in progress can be modified");
        }

        if (quantityToReturn <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (quantityToReturn > detail.getQuantity()) {
            throw new RuntimeException("Cannot return more than assigned quantity");
        }

        SparePart sparePart = detail.getSparePart();

        // devolver al inventario
        sparePart.setStock(
                sparePart.getStock() + quantityToReturn
        );

        // descontar del total
        BigDecimal amountToSubtract =
                detail.getUnitPrice()
                        .multiply(BigDecimal.valueOf(quantityToReturn));

        workOrder.setTotalValue(
                workOrder.getTotalValue().subtract(amountToSubtract)
        );

        // actualizar cantidad del detalle
        int newQuantity =
                detail.getQuantity() - quantityToReturn;

        if (newQuantity == 0) {

            workOrder.getDetails().remove(detail);
            workOrderDetailRepository.delete(detail);

        } else {

            detail.setQuantity(newQuantity);
        }

        workOrder.setUpdatedAt(LocalDateTime.now());
    }

    public WorkOrderResponse findById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found"));
        return  toResponse(workOrder);
    }
    public List<WorkOrderResponse> findAll() {
        return workOrderRepository.findAll()
                .stream()
                .map(WorkOrderResponse::toResponse)
                .toList();
    }
    public List<WorkOrderResponse> findAllNotFinalizado() {
        return workOrderRepository.findAll().stream()
                .filter(wo -> !"Finalizado".equalsIgnoreCase(wo.getStatus().getName()))
                .map(WorkOrderResponse::toResponse)
                .toList();
    }
    public  List<WorkOrderResponse> findByCustomerClient(String customerName) {
        return workOrderRepository.findByCustomerNameContainingIgnoreCase(customerName)
                .stream()
                .map(WorkOrderResponse::toResponse)
                .toList();
    }
    public  List<WorkOrderResponse> findByPlate(String motorcyclePlate) {
        return workOrderRepository.findByMotorcyclePlateContainingIgnoreCase(motorcyclePlate)
                .stream()
                .map(WorkOrderResponse::toResponse)
                .toList();
    }
    public List<WorkOrderResponse> findByStatus(String status) {
        return workOrderRepository.findByStatusName(status)
                .stream()
                .map(WorkOrderResponse::toResponse)
                .toList();
    }
    public List<WorkOrderResponse> findByOrderNumber(String orderNumber) {
        return workOrderRepository.findByOrderNumber(orderNumber)
                .stream()
                .map(WorkOrderResponse::toResponse)
                .toList();
    }





    @Transactional
    public WorkOrder updateStatus(Long workOrderId, Long statusId) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        workOrder.setStatus(status);
        workOrder.setUpdatedAt(LocalDateTime.now());

        return workOrderRepository.save(workOrder);
    }





}
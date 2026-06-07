package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.dto.AddSparePartRequest;
import org.biker.dto.WorkOrderDetailRequest;
import org.biker.dto.WorkOrderDetailResponse;
import org.biker.dto.WorkOrderRequest;
import org.biker.model.*;
import org.biker.repository.BrandRepository;
import org.biker.repository.SparePartRepository;
import org.biker.repository.StatusRepository;
import org.biker.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final BrandRepository brandRepository;
    private final StatusRepository statusRepository;
    private final SparePartRepository sparePartRepository;

    public WorkOrder create(WorkOrderRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Status status = statusRepository.findByName("EN_PROGRESO")
                .orElseThrow(() -> new RuntimeException("Status not found"));

        WorkOrder workOrder = new WorkOrder();

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

        if (!workOrder.getStatus().getName().equals("EN_PROGRESO")) {
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
                detail.getSparePart().getId()
        );
    }

   /* public List<WorkOrder> findAll() {
        return workOrderRepository.findAll();
    }

    public WorkOrder findById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work order not found"));
    }

    public void delete(Long id) {
        workOrderRepository.deleteById(id);
    }

    public WorkOrder updateStatus(Long id, Long statusId) {

        WorkOrder workOrder = findById(id);

        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));

        workOrder.setStatus(status);

        return workOrderRepository.save(workOrder);
    }*/
}
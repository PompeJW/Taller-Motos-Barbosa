package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.model.WorkOrder;
import org.biker.model.WorkOrderPaymentStatus;
import org.biker.service.WorkOrderPaymentStatusService;
import org.biker.repository.WorkOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-orders/payment-status")
@RequiredArgsConstructor
public class WorkOrderPaymentStatusController {

    private final WorkOrderPaymentStatusService paymentStatusService;
    private final WorkOrderRepository workOrderRepository;

    // ✅ Actualizar estado de pago de una orden
    @PutMapping("/{id}/update")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada"));

        paymentStatusService.updateWorkOrderPaymentStatus(workOrder);

        return ResponseEntity.ok("Estado de pago actualizado correctamente");
    }

    // ✅ Consultar estado de pago actual de una orden
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderPaymentStatus> getPaymentStatus(@PathVariable Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de trabajo no encontrada"));

        return ResponseEntity.ok(workOrder.getPaymentStatus());
    }
}

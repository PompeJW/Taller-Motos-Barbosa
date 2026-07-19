package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.model.WorkOrder;
import org.biker.model.WorkOrderPaymentStatus;
import org.biker.repository.PaymentRepository;
import org.biker.repository.WorkOrderPaymentStatusRepository;
import org.biker.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderPaymentStatusService {

    private final WorkOrderPaymentStatusRepository statusRepository;
    private final PaymentRepository paymentRepository;
    private final WorkOrderRepository workOrderRepository;




    public void updateWorkOrderPaymentStatus(WorkOrder workOrder) {
        double totalPagado = paymentRepository.findByWorkOrderId(workOrder.getId())
                .stream()
                .mapToDouble(p -> p.getAmount().doubleValue())
                .sum();

        double totalOrden = workOrder.getTotalValue().doubleValue();

        WorkOrderPaymentStatus status;
        if (totalPagado >= totalOrden) {
            status = statusRepository.findById(2L).orElseThrow();
        } else if (totalPagado > 0) {
            status = statusRepository.findById(3L).orElseThrow();
        } else {
            status = statusRepository.findById(1L).orElseThrow();
        }

        workOrder.setPaymentStatus(status);
        workOrderRepository.save(workOrder);
    }

}

package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.model.WorkOrder;
import org.biker.model.WorkOrderPaymentStatus;
import org.biker.repository.PaymentRepository;
import org.biker.repository.WorkOrderPaymentStatusRepository;
import org.biker.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import org.biker.model.Payment;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderPaymentStatusService {

    private final WorkOrderPaymentStatusRepository statusRepository;
    private final PaymentRepository paymentRepository;
    private final WorkOrderRepository workOrderRepository;




    public void updateWorkOrderPaymentStatus(WorkOrder workOrder) {
        BigDecimal totalPagado = paymentRepository.findByWorkOrderId(workOrder.getId())
                .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOrden = workOrder.getTotalValue();

        WorkOrderPaymentStatus status;
        if (totalPagado.compareTo(totalOrden) >= 0) {
            status = statusRepository.findFirstByStatusIgnoreCase("Pagado").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Pagado"));
        } else if (totalPagado.signum() > 0) {
            status = statusRepository.findFirstByStatusIgnoreCase("Abonado").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Abonado"));
        } else {
            status = statusRepository.findFirstByStatusIgnoreCase("Pendiente").orElseThrow(() -> new IllegalStateException("Falta estado de pago: Pendiente"));
        }

        workOrder.setPaymentStatus(status);
        workOrderRepository.save(workOrder);
    }

}

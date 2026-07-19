package org.biker.repository;

import org.biker.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Buscar todos los pagos de una orden de trabajo
    List<Payment> findByWorkOrderId(Long workOrderId);

    // Buscar todos los pagos de un cliente
    List<Payment> findByClientId(Long clientId);

    // Buscar todos los pagos de una venta directa (repuesto)
    List<Payment> findBySparePartId(Long sparePartId);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

}

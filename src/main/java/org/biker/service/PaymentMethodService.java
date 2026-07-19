package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.PaymentMethodRequest;
import org.biker.dto.PaymentMethodResponse;
import org.biker.model.PaymentMethod;
import org.biker.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodResponse> findAll() {
        return paymentMethodRepository.findAll()
                .stream()
                .map(pm -> new PaymentMethodResponse(pm.getId(), pm.getName()))
                .toList();
    }

    public PaymentMethodResponse findById(Long id) {
        PaymentMethod pm = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
        return new PaymentMethodResponse(pm.getId(), pm.getName());
    }

    public PaymentMethodResponse save(PaymentMethodRequest request) {
        PaymentMethod pm = new PaymentMethod();
        pm.setName(request.name());

        PaymentMethod saved = paymentMethodRepository.save(pm);
        return new PaymentMethodResponse(saved.getId(), saved.getName());
    }

    public PaymentMethodResponse update(Long id, PaymentMethodRequest request) {
        PaymentMethod existing = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
        existing.setName(request.name());

        PaymentMethod updated = paymentMethodRepository.save(existing);
        return new PaymentMethodResponse(updated.getId(), updated.getName());
    }

    public void delete(Long id) {
        paymentMethodRepository.deleteById(id);
    }
}

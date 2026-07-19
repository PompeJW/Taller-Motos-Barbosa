package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.PaymentMethodRequest;
import org.biker.dto.PaymentMethodResponse;
import org.biker.service.PaymentMethodService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethodResponse> getAll() {
        return paymentMethodService.findAll();
    }

    @GetMapping("/{id}")
    public PaymentMethodResponse getById(@PathVariable Long id) {
        return paymentMethodService.findById(id);
    }

    @PostMapping
    public PaymentMethodResponse create(@RequestBody PaymentMethodRequest request) {
        return paymentMethodService.save(request);
    }

    @PutMapping("/{id}")
    public PaymentMethodResponse update(@PathVariable Long id, @RequestBody PaymentMethodRequest request) {
        return paymentMethodService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
    }
}

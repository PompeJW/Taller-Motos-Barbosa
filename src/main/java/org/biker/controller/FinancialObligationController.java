package org.biker.controller;

import org.biker.dto.*;
import org.biker.service.FinancialObligationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/financial-obligations")
public class FinancialObligationController {
    private final FinancialObligationService service;
    public FinancialObligationController(FinancialObligationService service) { this.service = service; }

    @PostMapping
    public FinancialObligationResponse create(@RequestBody FinancialObligationCreateRequest request) {
        return service.create(request);
    }
    @GetMapping
    public List<FinancialObligationResponse> all() { return service.all(); }
    @GetMapping("/{id}")
    public FinancialObligationResponse byId(@PathVariable Long id) { return service.byId(id); }
    @PostMapping("/{id}/payments")
    public FinancialObligationPaymentResponse pay(@PathVariable Long id,
            @RequestBody FinancialObligationPaymentRequest request) {
        return service.registerPayment(id, request);
    }
    @GetMapping("/{id}/payments")
    public List<FinancialObligationPaymentResponse> payments(@PathVariable Long id) {
        return service.payments(id);
    }
}

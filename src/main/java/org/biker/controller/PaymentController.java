package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.PaymentRequest;
import org.biker.dto.PaymentResponse;
import org.biker.dto.WorkOrderResponse;
import org.biker.service.PaymentService;
import org.biker.service.PrinterService;
import org.biker.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PrinterService printerService;
    private final WorkOrderService workOrderService;


    // Registrar una venta libre (ventanilla)
    @PostMapping("/sale")
    public ResponseEntity<PaymentService.SaleResponse> registerSale(@RequestBody PaymentService.SaleRequest request) {
        PaymentService.SaleResponse response = paymentService.registerSale(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/print")
    public ResponseEntity<Void> printPayment(@PathVariable Long id) {
        PaymentResponse payment = paymentService.findById(id);
        WorkOrderResponse workOrder = workOrderService.findById(payment.workOrderId());


        try {
            printerService.printPayment(workOrder, payment);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    // Registrar un pago (POST)
    @PostMapping
    public ResponseEntity<PaymentResponse> registerPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.registerPayment(request);
        return ResponseEntity.ok(response);
    }

    // Histórico de pagos por orden de trabajo (GET)
    @GetMapping("/work-order/{id}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByWorkOrder(@PathVariable Long id) {
        List<PaymentResponse> payments = paymentService.getPaymentsByWorkOrder(id);
        return ResponseEntity.ok(payments);
    }

    // Histórico de pagos por cliente (GET)
    @GetMapping("/client/{id}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByClient(@PathVariable Long id) {
        List<PaymentResponse> payments = paymentService.getPaymentsByClient(id);
        return ResponseEntity.ok(payments);
    }


    // Todas las ventas sin filtro
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllSales() {
        return ResponseEntity.ok(paymentService.getAllSales());
    }

    // Ventas filtradas por día
    @GetMapping("/day/{date}")
    public ResponseEntity<List<PaymentResponse>> getSalesByDay(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date); // formato yyyy-MM-dd
        return ResponseEntity.ok(paymentService.getSalesByDay(localDate));
    }

    // Ventas filtradas por semana
    @GetMapping("/week/{year}/{week}")
    public ResponseEntity<List<PaymentResponse>> getSalesByWeek(@PathVariable int year, @PathVariable int week) {
        return ResponseEntity.ok(paymentService.getSalesByWeek(year, week));
    }

    // Ventas filtradas por mes
    @GetMapping("/month/{year}/{month}")
    public ResponseEntity<List<PaymentResponse>> getSalesByMonth(@PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(paymentService.getSalesByMonth(year, month));
    }
}

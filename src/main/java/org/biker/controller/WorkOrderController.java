package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.*;
import org.biker.model.WorkOrder;
import org.biker.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrderResponse create(@RequestBody WorkOrderRequest request) {

        return WorkOrderResponse.toResponse(workOrderService.create(request));
    }

    @PostMapping("/{id}/details")
    public WorkOrderDetailResponse addDetail(
            @PathVariable Long id,
            @RequestBody AddSparePartRequest request) {

        return workOrderService.addDetail(id, request);
    }
    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<Void> returnSparePart(
            @PathVariable Long detailId) {
        workOrderService.returnSparePart(detailId);
        return  ResponseEntity.noContent().build();
    }
    @PatchMapping("/details/{detailId}/return")
    public ResponseEntity<Void> returnSparePart(
            @PathVariable Long detailId,
            @RequestBody ReturnSparePartRequest request) {

        workOrderService.returnSparePartSpecific(
                detailId,
                request.quantity()
        );

        return ResponseEntity.noContent().build();
    }

   @GetMapping
    public List<WorkOrderResponse> findAll() {
        return workOrderService.findAll();
    }

    @GetMapping("/{id}")
    public WorkOrderResponse findById(@PathVariable Long id) {
        return workOrderService.findById(id);
    }

    @PutMapping("/{id}/status")
    public WorkOrder updateStatus(
            @PathVariable Long id,
            @RequestParam Long statusId) {
        return workOrderService.updateStatus(id,statusId);
    }
    @GetMapping("/not-finalizado")
    public List<WorkOrderResponse> findAllNotFinalizado() {
        return workOrderService.findAllNotFinalizado();
    }

    @GetMapping("/byName")
    public List<WorkOrderResponse> findByCustomerName(@RequestParam  String name) {
        return workOrderService.findByCustomerClient(name);
    }
    @GetMapping("/plate")
    public List<WorkOrderResponse> findByPlate(@RequestParam String plate) {
        return workOrderService.findByPlate(plate);
    }

    @GetMapping("/status")
    public List<WorkOrderResponse> findByStatus(@RequestParam String status) {
        return workOrderService.findByStatus(status);
    }

    @GetMapping("/order-number")
    public List<WorkOrderResponse> findByOrderNumber(@RequestParam String orderNumber){
        return workOrderService.findByOrderNumber(orderNumber);
    }

    @PatchMapping("/details/{detailId}/price")
    public ResponseEntity<Void> updateDetailPrice(
            @PathVariable Long detailId,
            @RequestBody UpdatePriceRequest request) {

        workOrderService.updateDetailPrice(detailId, request.newPrice());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<WorkOrderResponse> finalizarOrden(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal labourCost) {

        WorkOrder workOrder = workOrderService.finalizarOrden(id, labourCost);
        return ResponseEntity.ok(WorkOrderResponse.toResponse(workOrder));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return ResponseEntity.noContent().build();
    }



}

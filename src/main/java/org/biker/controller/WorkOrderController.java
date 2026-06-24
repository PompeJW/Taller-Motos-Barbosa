package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.*;
import org.biker.model.WorkOrder;
import org.biker.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkOrder create(@RequestBody WorkOrderRequest request) {
        return workOrderService.create(request);
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



}

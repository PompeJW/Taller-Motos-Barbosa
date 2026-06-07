package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.AddSparePartRequest;
import org.biker.dto.WorkOrderDetailResponse;
import org.biker.dto.WorkOrderRequest;
import org.biker.model.WorkOrder;
import org.biker.service.WorkOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

   /* @GetMapping
    public List<WorkOrder> findAll() {
        return workOrderService.findAll();
    }

    @GetMapping("/{id}")
    public WorkOrder findById(@PathVariable Long id) {
        return workOrderService.findById(id);
    }

    @PutMapping("/{id}/status")
    public WorkOrder updateStatus(
            @PathVariable Long id,
            @RequestParam Long statusId) {
        return workOrderService.updateStatus(id, statusId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workOrderService.delete(id);
    }*/
}

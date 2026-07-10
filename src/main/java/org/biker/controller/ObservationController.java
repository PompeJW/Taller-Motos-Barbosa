package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.ObservationRequest;
import org.biker.dto.ObservationResponse;
import org.biker.model.Observation;
import org.biker.service.ObservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/work-orders/{workOrderId}/observations")
    @RequiredArgsConstructor
    public class ObservationController {

        private final ObservationService observationService;

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public ObservationResponse addObservation(
                @PathVariable Long workOrderId,
                @RequestBody ObservationRequest request) {

            return observationService.addObservation(workOrderId, request);
        }

        @GetMapping
        public List<ObservationResponse> findByWorkOrder(@PathVariable Long workOrderId) {
            return observationService.findByWorkOrder(workOrderId)
                    .stream()
                    .map(o -> new ObservationResponse(o.getId(), o.getDescription()))
                    .toList();
        }

    }

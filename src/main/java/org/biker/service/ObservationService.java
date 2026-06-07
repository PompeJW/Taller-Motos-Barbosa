package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.dto.ObservationRequest;
import org.biker.dto.ObservationResponse;
import org.biker.model.Observation;
import org.biker.model.WorkOrder;
import org.biker.repository.ObservationRepository;
import org.biker.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ObservationService {

    private final ObservationRepository observationRepository;
    private final WorkOrderRepository workOrderRepository;

    public ObservationResponse addObservation(Long workOrderId, ObservationRequest request) {

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        Observation observation = new Observation();
        observation.setDescription(request.description());
        observation.setWorkOrder(workOrder);

        Observation saved = observationRepository.save(observation);

        return new ObservationResponse(
                saved.getId(),
                saved.getDescription()
        );
    }

    public List<Observation> findByWorkOrder(Long workOrderId) {
        return observationRepository.findByWorkOrderId(workOrderId);
    }
}

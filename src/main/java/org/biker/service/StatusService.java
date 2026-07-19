package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.StatusRequest;
import org.biker.dto.StatusResponse;
import org.biker.dto.WorkOrderResponse;
import org.biker.model.Status;
import org.biker.repository.StatusRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<Status> findAll() {
        return statusRepository.findAll();
    }

    public Status findById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.STATUS_NOT_FOUND)); // ✅ corregido
    }

    public StatusResponse save(StatusRequest request) {
        Status status = new Status();
        status.setName(request.name());

        Status saved = statusRepository.save(status);

        return new StatusResponse(
                saved.getId(),
                saved.getName()
        );
    }

    public List<StatusResponse> findStatusesForSparePart() {
        List<String> allowedStatuses = List.of(
                "Inventariado",
                "Descontinuado"
        );

        return statusRepository.findByNameIn(allowedStatuses)
                .stream()
                .map(status -> new StatusResponse(
                        status.getId(),
                        status.getName()
                ))
                .toList();
    }
}


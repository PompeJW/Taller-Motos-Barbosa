package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.StatusRequest;
import org.biker.dto.StatusResponse;
import org.biker.model.Status;
import org.biker.service.StatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService status;

    @GetMapping
    public List<Status> getAll() {
        return status.findAll();
    }

    @GetMapping("/{id}")
    public Status getById(@PathVariable Long id) {
        return status.findById(id);
    }

    @PostMapping
    public StatusResponse save(
            @RequestBody StatusRequest request) {

        return status.save(request);
    }

  /*  @PutMapping("/{id}")
    public BrandResponse update(
            @PathVariable Long id,
            @RequestBody BrandRequest request) {

        return service.update(id, request);
    }*/

}

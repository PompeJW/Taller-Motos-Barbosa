package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.OrderSparePartRequest;
import org.biker.dto.OrderSparePartResponse;
import org.biker.service.OrderSparePartService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order-spare-part")
@RequiredArgsConstructor
public class OrderSparePart {

    private final OrderSparePartService service;
    @PostMapping
    public OrderSparePartResponse save(
            @RequestBody OrderSparePartRequest request) {
        return  service.create(request);
    }
}

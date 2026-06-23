package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.OrderSparePartRequest;
import org.biker.dto.OrderSparePartResponse;
import org.biker.model.OrderSparePart;
import org.biker.model.SparePart;
import org.biker.model.Status;
import org.biker.repository.OrderSparePartRepository;
import org.biker.repository.SparePartRepository;
import org.biker.repository.StatusRepository;
import org.biker.utils.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSparePartService {

    private final OrderSparePartRepository repository;
    private final SparePartRepository sparePartRepository;
    private  final StatusRepository statusRepository;

    public List<OrderSparePart> findAll() {
        return  repository.findAll();
    }
    public OrderSparePartResponse create(OrderSparePartRequest request){

        SparePart sparePart = null;

        if (request.sparePartId() != null) {
            sparePart = sparePartRepository.findById(request.sparePartId())
                    .orElseThrow(() ->
                            new RuntimeException(Message.SPARE_PART_NOT_FOUND));
        }
        if (request.sparePartId() == null &&
                (request.repuesto() == null || request.repuesto().isBlank())) {

            throw new RuntimeException(
                    "Debe indicar un repuesto existente o escribir el nombre del repuesto");
        }

        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        OrderSparePart orderSparePart = new OrderSparePart();
        orderSparePart.setRepuesto(request.repuesto());
        orderSparePart.setCantidad(request.cantidad());
        orderSparePart.setSparePart(sparePart);
        orderSparePart.setStatus(status);

        OrderSparePart saved = repository.save(orderSparePart);


        String sparePartName = null;

        if(saved.getSparePart() != null){
            sparePartName = saved.getSparePart().getName();
        }
        String nombreRepuesto =
                saved.getSparePart() != null
                        ? saved.getSparePart().getName()
                        : saved.getRepuesto();

        return new OrderSparePartResponse(
                saved.getId(),
                nombreRepuesto,
                saved.getCantidad(),
                sparePartName,
                saved.getStatus().getName()
        );
    }

}

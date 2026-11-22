package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.biker.dto.*;
import org.biker.model.*;
import org.biker.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class InventoryEntryService {
    private final SparePartRepository spareParts;
    private final InventoryPurchaseHistoryRepository history;

    private SparePart get(Long id) {
        return spareParts.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
    }
    private void validate(InventoryEntryRequest r) {
        if(r==null || r.quantity()==null || r.quantity()<=0 || r.unitCost()==null || r.unitCost().signum()<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cantidad y costo de compra inválidos");
        if(r.salePrice()!=null && r.salePrice().signum()<0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Precio de venta inválido");
        if(r.note()!=null && r.note().length()>255)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La nota supera 255 caracteres");
    }
    private InventoryEntryPreview calculate(SparePart p, InventoryEntryRequest r) {
        int previousStock=p.getStock()==null?0:p.getStock();
        if(previousStock<0) throw new ResponseStatusException(HttpStatus.CONFLICT,"El stock actual es negativo");
        int total;
        try {total=Math.addExact(previousStock,r.quantity());}
        catch(ArithmeticException ex) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cantidad demasiado grande");}
        BigDecimal oldCost=p.getPriceBuy()==null?BigDecimal.ZERO:p.getPriceBuy();
        BigDecimal oldSale=p.getPriceSell()==null?BigDecimal.ZERO:p.getPriceSell();
        BigDecimal avg=oldCost.multiply(BigDecimal.valueOf(previousStock)).add(r.unitCost().multiply(BigDecimal.valueOf(r.quantity())))
                .divide(BigDecimal.valueOf(total),2,RoundingMode.HALF_UP);
        // Mantener ganancia absoluta por unidad como sugerencia; el usuario decide el precio final.
        BigDecimal proposed=avg.add(oldSale.subtract(oldCost)).max(BigDecimal.ZERO).setScale(2,RoundingMode.HALF_UP);
        BigDecimal selected=r.salePrice()==null?oldSale:r.salePrice();
        BigDecimal margin=selected.signum()==0?BigDecimal.ZERO:selected.subtract(avg).multiply(BigDecimal.valueOf(100)).divide(selected,2,RoundingMode.HALF_UP);
        return new InventoryEntryPreview(p.getId(),p.getName(),previousStock,r.quantity(),total,oldCost,r.unitCost(),avg,oldSale,proposed,margin);
    }
    @Transactional(readOnly=true)
    public InventoryEntryPreview preview(Long id, InventoryEntryRequest request) {
        validate(request);return calculate(get(id),request);
    }
    @Transactional
    public InventoryEntryPreview receive(Long id, InventoryEntryRequest request) {
        validate(request);
        SparePart p=spareParts.findByIdForUpdate(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado"));
        InventoryEntryPreview v=calculate(p,request);
        if(request.salePrice()==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Confirma explícitamente el precio de venta (puede ser el actual)");
        InventoryPurchaseHistory h=new InventoryPurchaseHistory();
        h.setSparePart(p);h.setCreatedAt(LocalDateTime.now());h.setQuantity(request.quantity());h.setUnitCost(request.unitCost());
        h.setPreviousAverageCost(v.previousAverageCost());h.setNewAverageCost(v.resultingAverageCost());
        h.setPreviousSalePrice(v.currentSalePrice());h.setNewSalePrice(request.salePrice());
        h.setPreviousStock(v.previousStock());h.setNewStock(v.resultingStock());h.setNote(request.note());
        p.setStock(v.resultingStock());p.setPriceBuy(v.resultingAverageCost());p.setPriceSell(request.salePrice());
        spareParts.save(p);history.save(h);
        return v;
    }
    @Transactional(readOnly=true)
    public List<InventoryEntryHistoryResponse> history(Long id) {
        get(id);
        return history.findBySparePart_IdOrderByCreatedAtDescIdDesc(id).stream().map(h->new InventoryEntryHistoryResponse(
                h.getId(),h.getCreatedAt(),h.getQuantity(),h.getUnitCost(),h.getPreviousAverageCost(),h.getNewAverageCost(),
                h.getPreviousSalePrice(),h.getNewSalePrice(),h.getPreviousStock(),h.getNewStock(),h.getNote())).toList();
    }
}

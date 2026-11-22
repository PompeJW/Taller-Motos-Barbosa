package org.biker.controller;
import lombok.RequiredArgsConstructor;
import org.biker.dto.*;
import org.biker.service.InventoryEntryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/spare-part/{id}/entries") @RequiredArgsConstructor
public class InventoryEntryController {
    private final InventoryEntryService service;
    @PostMapping("/preview") public InventoryEntryPreview preview(@PathVariable Long id,@RequestBody InventoryEntryRequest request) {return service.preview(id,request);}
    @PostMapping public InventoryEntryPreview receive(@PathVariable Long id,@RequestBody InventoryEntryRequest request) {return service.receive(id,request);}
    @GetMapping public List<InventoryEntryHistoryResponse> history(@PathVariable Long id) {return service.history(id);}
}

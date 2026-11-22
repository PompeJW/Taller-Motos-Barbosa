package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.service.PrintQueueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/print-queue")
@RequiredArgsConstructor
public class PrintQueueController {
    private final PrintQueueService queue;
    @Value("${biker.print.token:}") private String token;
    private void verify(String supplied) {
        if(token.isBlank() || supplied==null || !java.security.MessageDigest.isEqual(token.getBytes(java.nio.charset.StandardCharsets.UTF_8), supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token de impresión inválido");
    }
    @PostMapping("/claim") public Map<String,Object> claim(@RequestHeader(value="X-Biker-Token",required=false) String supplied) {verify(supplied);return queue.claim();}
    @PostMapping("/{id}/ack") public Map<String,String> ack(@PathVariable long id,@RequestHeader(value="X-Biker-Token",required=false) String supplied,@RequestBody Map<String,Object> body) {
        verify(supplied);queue.acknowledge(id,Boolean.TRUE.equals(body.get("success")),String.valueOf(body.getOrDefault("message","")));return Map.of("status","ok");
    }
    @GetMapping("/sale/{saleId}") public Map<String,String> status(@PathVariable long saleId) {return Map.of("status",queue.status(saleId));}
    @PostMapping("/sale/{saleId}/reprint") public Map<String,String> reprint(@PathVariable long saleId) {queue.requeue(saleId);return Map.of("status","PENDING");}
}

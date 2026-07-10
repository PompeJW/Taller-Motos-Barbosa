package org.biker.controller;

import lombok.RequiredArgsConstructor;
import org.biker.dto.ClientRequest;
import org.biker.dto.ClientResponse;
import org.biker.model.Client;
import org.biker.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody ClientRequest request) {
        Client client = new Client();
        client.setClientName(request.clientName());
        client.setDocumento(request.documento());
        client = clientRepository.save(client);

        return ResponseEntity.ok(new ClientResponse(client.getId(), client.getClientName(), client.getDocumento()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return ResponseEntity.ok(new ClientResponse(client.getId(), client.getClientName(), client.getDocumento()));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
        List<ClientResponse> clients = clientRepository.findAll()
                .stream()
                .map(c -> new ClientResponse(c.getId(), c.getClientName(), c.getDocumento()))
                .toList();
        return ResponseEntity.ok(clients);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientResponse>> searchByName(@RequestParam String name) {
        List<ClientResponse> clients = clientRepository.findByClientNameContainingIgnoreCase(name)
                .stream()
                .map(c -> new ClientResponse(c.getId(), c.getClientName(), c.getDocumento()))
                .toList();
        return ResponseEntity.ok(clients);
    }

}

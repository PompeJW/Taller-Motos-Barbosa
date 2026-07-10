package org.biker.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.biker.dto.ClientRequest;
import org.biker.dto.ClientResponse;
import org.biker.model.Client;
import org.biker.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse create(ClientRequest request) {
        Client client = new Client();
        client.setClientName(request.clientName());
        client.setDocumento(request.documento());
        client = clientRepository.save(client);

        return new ClientResponse(client.getId(), client.getClientName(), client.getDocumento());
    }

    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return new ClientResponse(client.getId(), client.getClientName(), client.getDocumento());
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(c -> new ClientResponse(c.getId(), c.getClientName(), c.getDocumento()))
                .toList();
    }

    // 🔎 Buscar por nombre parecido (like / contains / ignore case)
    public List<ClientResponse> searchByName(String name) {
        return clientRepository.findByClientNameIgnoreCase(name)
                .map(c -> List.of(new ClientResponse(c.getId(), c.getClientName(), c.getDocumento())))
                .orElseGet(List::of);
    }

    // 🔎 Buscar por documento
    public ClientResponse searchByDocumento(String documento) {
        Client client = clientRepository.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return new ClientResponse(client.getId(), client.getClientName(), client.getDocumento());
    }
}

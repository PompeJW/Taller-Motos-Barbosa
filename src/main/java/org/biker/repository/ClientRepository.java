package org.biker.repository;

import org.biker.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByDocumento(String documento);
    Optional<Client> findByClientNameIgnoreCase(String clientName);
    List<Client> findByClientNameContainingIgnoreCase(String clientName); // 🔎 búsqueda parcial
}

package org.biker.repository;

import org.biker.model.Status;
import org.biker.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByName(String name);

    List<Status> findByNameIn(List<String> names);
}

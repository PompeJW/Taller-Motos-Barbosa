package org.biker.repository;

import org.biker.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

    List<Observation> findByWorkOrderId(Long workOrderId);
}

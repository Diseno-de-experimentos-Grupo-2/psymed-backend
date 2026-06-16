package com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IoTAlertRepository extends JpaRepository<IoTAlert, Long> {
    List<IoTAlert> findByPatientIdOrderByDetectedAtDesc(Long patientId);
    Optional<IoTAlert> findByEdgeEventId(String edgeEventId);
    boolean existsByEdgeEventId(String edgeEventId);
}

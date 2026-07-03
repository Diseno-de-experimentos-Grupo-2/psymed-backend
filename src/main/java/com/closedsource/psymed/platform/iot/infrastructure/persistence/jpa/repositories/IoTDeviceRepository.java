package com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IoTDeviceRepository extends JpaRepository<IoTDevice, Long> {
    Optional<IoTDevice> findByDeviceId(String deviceId);
    Optional<IoTDevice> findByPairingCode(String pairingCode);
    Optional<IoTDevice> findByPatientId(Long patientId);
    boolean existsByDeviceId(String deviceId);
}

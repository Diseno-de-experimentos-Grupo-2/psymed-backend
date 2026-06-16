package com.closedsource.psymed.platform.iot.domain.model.aggregates;

import com.closedsource.psymed.platform.iot.domain.model.commands.RegisterIoTDeviceCommand;
import com.closedsource.psymed.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Entity
@Table(name = "iot_devices")
public class IoTDevice extends AuditableAbstractAggregateRoot<IoTDevice> {

    @Column(unique = true, nullable = false)
    @Getter
    private String deviceId;

    @Column
    @Getter
    private Long patientId;

    // TODO(producción): generar pairing_code aleatorio y seguro al registrar el hardware en fábrica (CLI/admin).
    // En el prototipo se usa un código fijo en el seeder; la validación en claim() permanece igual.
    @Column(length = 16)
    @Getter
    private String pairingCode;

    @Column
    @Getter
    private String edgeLocation;

    @Column(nullable = false)
    @Getter
    private boolean active = true;

    @Column
    @Getter
    private Instant lastSeenAt;

    public IoTDevice(RegisterIoTDeviceCommand command) {
        this.deviceId = command.deviceId();
        this.patientId = command.patientId();
        this.pairingCode = command.pairingCode();
        this.edgeLocation = command.edgeLocation();
        this.active = true;
    }

    public static IoTDevice createUnclaimed(String deviceId, String pairingCode, String edgeLocation) {
        var device = new IoTDevice();
        device.deviceId = deviceId;
        device.pairingCode = pairingCode;
        device.edgeLocation = edgeLocation;
        device.active = true;
        return device;
    }

    public boolean isClaimed() {
        return patientId != null;
    }

    public boolean matchesPairingCode(String code) {
        return pairingCode != null && pairingCode.equals(code);
    }

    public void claim(Long newPatientId) {
        if (isClaimed()) {
            throw new IllegalStateException("Device already claimed");
        }
        this.patientId = newPatientId;
    }

    public void markSeen() {
        this.lastSeenAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
    }
}

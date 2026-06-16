package com.closedsource.psymed.platform.iot.domain.model.aggregates;

import com.closedsource.psymed.platform.iot.domain.model.commands.ReceiveAlertFromEdgeCommand;
import com.closedsource.psymed.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Entity
@Table(name = "iot_alerts")
public class IoTAlert extends AuditableAbstractAggregateRoot<IoTAlert> {

    @Column(nullable = false)
    @Getter
    private Long patientId;

    @Column(nullable = false)
    @Getter
    private String deviceId;

    @Column(unique = true, nullable = false)
    @Getter
    private String edgeEventId;

    @Column(nullable = false)
    @Getter
    private String alertType;

    @Column(nullable = false)
    @Getter
    private String severity;

    @Column(columnDefinition = "TEXT")
    @Getter
    private String message;

    @Column
    @Getter
    private Instant detectedAt;

    @Column
    @Getter
    private Instant receivedAt;

    @Column(nullable = false)
    @Getter
    private String alertStatus = "new";

    @Column
    @Getter
    private Integer heartRateBpm;

    @Column
    @Getter
    private Double temperatureC;

    @Column
    @Getter
    private Double humidityPercent;

    public IoTAlert(ReceiveAlertFromEdgeCommand cmd) {
        this.patientId = cmd.patientId();
        this.deviceId = cmd.deviceId();
        this.edgeEventId = cmd.edgeEventId();
        this.alertType = cmd.alertType();
        this.severity = cmd.severity();
        this.message = cmd.message();
        this.detectedAt = cmd.detectedAt();
        this.receivedAt = Instant.now();
        this.heartRateBpm = cmd.heartRateBpm();
        this.temperatureC = cmd.temperatureC();
        this.humidityPercent = cmd.humidityPercent();
    }

    public void markSeen() {
        this.alertStatus = "seen";
    }

    public void markResolved() {
        this.alertStatus = "resolved";
    }
}

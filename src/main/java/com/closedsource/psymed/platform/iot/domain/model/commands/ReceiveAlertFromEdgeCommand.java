package com.closedsource.psymed.platform.iot.domain.model.commands;

import java.time.Instant;

public record ReceiveAlertFromEdgeCommand(
    String edgeEventId,
    String deviceId,
    Long patientId,
    String alertType,
    String severity,
    String message,
    Instant detectedAt,
    Integer heartRateBpm,
    Double temperatureC,
    Double humidityPercent
) {}

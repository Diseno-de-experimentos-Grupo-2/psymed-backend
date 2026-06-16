package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import java.time.Instant;

public record IoTAlertResource(
    Long id,
    String alertType,
    String severity,
    String message,
    Instant detectedAt,
    String alertStatus,
    Integer heartRateBpm,
    Double temperatureC
) {}

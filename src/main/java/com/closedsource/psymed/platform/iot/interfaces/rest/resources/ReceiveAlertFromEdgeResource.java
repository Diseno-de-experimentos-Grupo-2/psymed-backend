package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import java.time.Instant;
import java.util.Map;

public record ReceiveAlertFromEdgeResource(
    String edgeEventId,
    String deviceId,
    Long patientId,
    String type,
    String severity,
    String message,
    Instant detectedAt,
    Map<String, Object> metrics
) {}

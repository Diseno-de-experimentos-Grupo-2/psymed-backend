package com.closedsource.psymed.platform.iot.interfaces.rest.transform;

import com.closedsource.psymed.platform.iot.domain.model.commands.ReceiveAlertFromEdgeCommand;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ReceiveAlertFromEdgeResource;

import java.util.Map;

public class ReceiveAlertCommandFromResourceAssembler {
    public static ReceiveAlertFromEdgeCommand toCommand(ReceiveAlertFromEdgeResource resource) {
        Map<String, Object> metrics = resource.metrics() != null ? resource.metrics() : Map.of();
        return new ReceiveAlertFromEdgeCommand(
            resource.edgeEventId(),
            resource.deviceId(),
            resource.patientId(),
            resource.type(),
            resource.severity(),
            resource.message(),
            resource.detectedAt(),
            metrics.containsKey("heart_rate_bpm") ? ((Number) metrics.get("heart_rate_bpm")).intValue() : null,
            metrics.containsKey("temperature_c") ? ((Number) metrics.get("temperature_c")).doubleValue() : null,
            metrics.containsKey("humidity_percent") ? ((Number) metrics.get("humidity_percent")).doubleValue() : null
        );
    }
}

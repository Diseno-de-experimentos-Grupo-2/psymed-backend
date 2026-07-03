package com.closedsource.psymed.platform.iot.interfaces.rest.transform;

import com.closedsource.psymed.platform.iot.domain.model.commands.ReceiveAlertFromEdgeCommand;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ReceiveAlertFromEdgeResource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
            parseInstant(resource.detectedAt()),
            intOrNull(metrics.get("heart_rate_bpm")),
            doubleOrNull(metrics.get("temperature_c")),
            doubleOrNull(metrics.get("humidity_percent"))
        );
    }

    private static Integer intOrNull(Object value) {
        return value instanceof Number n ? n.intValue() : null;
    }

    private static Double doubleOrNull(Object value) {
        return value instanceof Number n ? n.doubleValue() : null;
    }

    /**
     * Parses timestamps from the edge, which may be naive ISO (no zone), offset, or UTC 'Z'.
     */
    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) return Instant.now();
        try { return Instant.parse(value); } catch (Exception ignored) {}
        try { return OffsetDateTime.parse(value).toInstant(); } catch (Exception ignored) {}
        try { return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC); } catch (Exception ignored) {}
        return Instant.now();
    }
}

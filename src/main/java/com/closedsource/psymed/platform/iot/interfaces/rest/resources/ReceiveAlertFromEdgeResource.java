package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;

/**
 * Payload sent by the PsyMed Edge Server. The edge uses snake_case keys
 * (edge_event_id, patient_id, detected_at, ...), so we map them with the
 * snake-case naming strategy. detectedAt is kept as a String and parsed
 * leniently in the assembler because the edge emits naive ISO timestamps
 * (no zone/offset), which would fail strict Instant parsing.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReceiveAlertFromEdgeResource(
    String edgeEventId,
    String deviceId,
    Long patientId,
    String type,
    String severity,
    String message,
    String detectedAt,
    Map<String, Object> metrics
) {}

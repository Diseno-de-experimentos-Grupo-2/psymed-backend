package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;
import java.util.Map;

/**
 * Payload sent by the PsyMed Edge Server (snake_case keys: edge_summary_id,
 * patient_id, risk_level, summary_text, ...).
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReceiveDailySummaryResource(
    String edgeSummaryId,
    String deviceId,
    Long patientId,
    LocalDate date,
    String riskLevel,
    String summaryText,
    Map<String, Object> metrics
) {}

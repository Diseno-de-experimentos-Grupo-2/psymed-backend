package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.Map;

public record ReceiveDailySummaryResource(
    String edgeSummaryId,
    Long patientId,
    LocalDate date,
    String riskLevel,
    String summaryText,
    Map<String, Object> metrics
) {}

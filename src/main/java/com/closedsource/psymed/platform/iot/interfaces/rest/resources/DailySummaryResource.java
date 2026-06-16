package com.closedsource.psymed.platform.iot.interfaces.rest.resources;

import java.time.LocalDate;

public record DailySummaryResource(
    Long patientId,
    LocalDate date,
    String riskLevel,
    String summaryText,
    Integer totalReadings,
    Double avgHeartRate,
    Integer maxHeartRate,
    Double avgTemperatureC,
    Integer alertCount,
    Integer disconnectedMinutes
) {}

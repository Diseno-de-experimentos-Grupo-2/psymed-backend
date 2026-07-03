package com.closedsource.psymed.platform.iot.domain.model.commands;

import java.time.LocalDate;

public record ReceiveDailySummaryCommand(
    String edgeSummaryId,
    String deviceId,
    Long patientId,
    LocalDate date,
    String riskLevel,
    String summaryText,
    Integer totalReadings,
    Double avgHeartRate,
    Integer maxHeartRate,
    Double avgTemperatureC,
    Integer alertCount,
    Integer disconnectedMinutes,
    String metricsJson
) {}

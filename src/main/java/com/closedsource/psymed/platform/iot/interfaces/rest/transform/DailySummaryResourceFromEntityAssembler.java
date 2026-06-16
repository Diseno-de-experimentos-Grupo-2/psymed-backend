package com.closedsource.psymed.platform.iot.interfaces.rest.transform;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.DailySummaryResource;

public class DailySummaryResourceFromEntityAssembler {
    public static DailySummaryResource toResource(DailyHealthSummary summary) {
        return new DailySummaryResource(
            summary.getPatientId(),
            summary.getSummaryDate(),
            summary.getRiskLevel(),
            summary.getSummaryText(),
            summary.getTotalReadings(),
            summary.getAvgHeartRate(),
            summary.getMaxHeartRate(),
            summary.getAvgTemperatureC(),
            summary.getAlertCount(),
            summary.getDisconnectedMinutes()
        );
    }
}

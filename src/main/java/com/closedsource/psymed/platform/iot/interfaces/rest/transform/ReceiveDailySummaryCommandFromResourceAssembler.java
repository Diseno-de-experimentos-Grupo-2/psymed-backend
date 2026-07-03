package com.closedsource.psymed.platform.iot.interfaces.rest.transform;

import com.closedsource.psymed.platform.iot.domain.model.commands.ReceiveDailySummaryCommand;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ReceiveDailySummaryResource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ReceiveDailySummaryCommandFromResourceAssembler {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ReceiveDailySummaryCommand toCommand(ReceiveDailySummaryResource resource) {
        Map<String, Object> m = resource.metrics() != null ? resource.metrics() : Map.of();
        String metricsJson;
        try {
            metricsJson = MAPPER.writeValueAsString(m);
        } catch (Exception e) {
            metricsJson = "{}";
        }
        return new ReceiveDailySummaryCommand(
            resource.edgeSummaryId(),
            resource.deviceId(),
            resource.patientId(),
            resource.date(),
            resource.riskLevel(),
            resource.summaryText(),
            intOrNull(m.get("total_readings")),
            doubleOrNull(m.get("avg_heart_rate")),
            intOrNull(m.get("max_heart_rate")),
            doubleOrNull(m.get("avg_temperature_c")),
            intOrNull(m.get("alert_count")),
            intOrNull(m.get("disconnected_minutes")),
            metricsJson
        );
    }

    private static Integer intOrNull(Object value) {
        return value instanceof Number n ? n.intValue() : null;
    }

    private static Double doubleOrNull(Object value) {
        return value instanceof Number n ? n.doubleValue() : null;
    }
}

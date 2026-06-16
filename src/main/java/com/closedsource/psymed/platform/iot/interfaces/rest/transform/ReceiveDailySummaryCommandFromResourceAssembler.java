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
            resource.patientId(),
            resource.date(),
            resource.riskLevel(),
            resource.summaryText(),
            m.containsKey("total_readings") ? ((Number) m.get("total_readings")).intValue() : null,
            m.containsKey("avg_heart_rate") ? ((Number) m.get("avg_heart_rate")).doubleValue() : null,
            m.containsKey("max_heart_rate") ? ((Number) m.get("max_heart_rate")).intValue() : null,
            m.containsKey("avg_temperature_c") ? ((Number) m.get("avg_temperature_c")).doubleValue() : null,
            m.containsKey("alert_count") ? ((Number) m.get("alert_count")).intValue() : null,
            m.containsKey("disconnected_minutes") ? ((Number) m.get("disconnected_minutes")).intValue() : null,
            metricsJson
        );
    }
}

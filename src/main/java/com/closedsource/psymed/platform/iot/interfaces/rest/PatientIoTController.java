package com.closedsource.psymed.platform.iot.interfaces.rest;

import com.closedsource.psymed.platform.iot.domain.model.commands.UpdateAlertStatusCommand;
import com.closedsource.psymed.platform.iot.domain.model.queries.GetAlertsByPatientIdQuery;
import com.closedsource.psymed.platform.iot.domain.model.queries.GetDailySummaryByPatientAndDateQuery;
import com.closedsource.psymed.platform.iot.domain.model.queries.GetDeviceByPatientIdQuery;
import com.closedsource.psymed.platform.iot.domain.services.IoTCommandService;
import com.closedsource.psymed.platform.iot.domain.services.IoTQueryService;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.DailySummaryResource;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.IoTAlertResource;
import com.closedsource.psymed.platform.iot.interfaces.rest.transform.DailySummaryResourceFromEntityAssembler;
import com.closedsource.psymed.platform.iot.interfaces.rest.transform.IoTAlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/patient", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Patient IoT", description = "Patient IoT dashboard endpoints for Flutter")
public class PatientIoTController {

    private final IoTQueryService iotQueryService;
    private final IoTCommandService iotCommandService;

    public PatientIoTController(IoTQueryService iotQueryService, IoTCommandService iotCommandService) {
        this.iotQueryService = iotQueryService;
        this.iotCommandService = iotCommandService;
    }

    @GetMapping("/{patientId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@PathVariable Long patientId) {
        var deviceOpt = iotQueryService.handle(new GetDeviceByPatientIdQuery(patientId));
        var todayOpt = iotQueryService.handle(new GetDailySummaryByPatientAndDateQuery(patientId, LocalDate.now()));
        var alerts = iotQueryService.handle(new GetAlertsByPatientIdQuery(patientId));
        var recentAlerts = alerts.stream()
            .limit(5)
            .map(IoTAlertResourceFromEntityAssembler::toResource)
            .collect(Collectors.toList());

        var deviceStatus = deviceOpt.map(d -> Map.of(
            "device_id", (Object) d.getDeviceId(),
            "online", d.getLastSeenAt() != null,
            "last_seen_at", d.getLastSeenAt() != null ? d.getLastSeenAt().toString() : ""
        )).orElse(Map.of("device_id", "", "online", false));

        var todaySummary = todayOpt.map(s -> Map.of(
            "risk_level", (Object) s.getRiskLevel(),
            "alert_count", s.getAlertCount(),
            "avg_heart_rate", s.getAvgHeartRate(),
            "max_heart_rate", s.getMaxHeartRate()
        )).orElse(Map.of("risk_level", "unknown", "alert_count", 0));

        return ResponseEntity.ok(Map.of(
            "patient_id", patientId,
            "current_status", todayOpt.map(s -> s.getRiskLevel()).orElse("unknown"),
            "last_device_status", deviceStatus,
            "today_summary", todaySummary,
            "recent_alerts", recentAlerts
        ));
    }

    @GetMapping("/{patientId}/alerts")
    public ResponseEntity<Map<String, Object>> getAlerts(@PathVariable Long patientId) {
        var resources = iotQueryService.handle(new GetAlertsByPatientIdQuery(patientId)).stream()
            .map(IoTAlertResourceFromEntityAssembler::toResource)
            .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("patient_id", patientId, "alerts", resources));
    }

    @GetMapping("/{patientId}/daily-summary")
    public ResponseEntity<DailySummaryResource> getDailySummary(
        @PathVariable Long patientId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var targetDate = date != null ? date : LocalDate.now();
        return iotQueryService.handle(new GetDailySummaryByPatientAndDateQuery(patientId, targetDate))
            .map(s -> ResponseEntity.ok(DailySummaryResourceFromEntityAssembler.toResource(s)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{patientId}/alerts/{alertId}/status")
    public ResponseEntity<IoTAlertResource> updateAlertStatus(
        @PathVariable Long patientId,
        @PathVariable Long alertId,
        @RequestBody Map<String, String> body
    ) {
        var newStatus = body.get("status");
        if (newStatus == null) return ResponseEntity.badRequest().build();
        return iotCommandService.handle(new UpdateAlertStatusCommand(alertId, newStatus))
            .map(a -> ResponseEntity.ok(IoTAlertResourceFromEntityAssembler.toResource(a)))
            .orElse(ResponseEntity.notFound().build());
    }
}

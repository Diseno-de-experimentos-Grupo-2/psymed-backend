package com.closedsource.psymed.platform.iot.interfaces.rest;

import com.closedsource.psymed.platform.iot.domain.model.queries.GetAlertsByPatientIdQuery;
import com.closedsource.psymed.platform.iot.domain.model.queries.GetDailySummaryByPatientAndDateQuery;
import com.closedsource.psymed.platform.iot.domain.services.IoTQueryService;
import com.closedsource.psymed.platform.profiles.domain.model.queries.GetPatientProfileByProfessionalIdQuery;
import com.closedsource.psymed.platform.profiles.domain.services.PatientProfileQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/psychiatrist", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Psychiatrist Monitoring", description = "Patient monitoring endpoints for psychiatrists")
public class PsychiatristMonitoringController {

    private final PatientProfileQueryService patientProfileQueryService;
    private final IoTQueryService iotQueryService;

    public PsychiatristMonitoringController(
        PatientProfileQueryService patientProfileQueryService,
        IoTQueryService iotQueryService
    ) {
        this.patientProfileQueryService = patientProfileQueryService;
        this.iotQueryService = iotQueryService;
    }

    @GetMapping("/patients/monitoring")
    public ResponseEntity<List<Map<String, Object>>> getPatientsMonitoring(@RequestParam Long professionalId) {
        var patientProfiles = patientProfileQueryService.handle(
            new GetPatientProfileByProfessionalIdQuery(professionalId)
        );

        var result = patientProfiles.stream().map(profile -> {
            Long patientId = profile.getId();
            var alerts = iotQueryService.handle(new GetAlertsByPatientIdQuery(patientId));
            long newAlerts = alerts.stream().filter(a -> "new".equals(a.getAlertStatus())).count();
            var todaySummary = iotQueryService.handle(
                new GetDailySummaryByPatientAndDateQuery(patientId, LocalDate.now())
            );

            return (Map<String, Object>) Map.of(
                "patient_id", patientId,
                "patient_name", profile.getFullName(),
                "new_alerts", newAlerts,
                "risk_level", todaySummary.map(s -> s.getRiskLevel()).orElse("unknown"),
                "alert_count_today", todaySummary.map(s -> s.getAlertCount()).orElse(0),
                "recent_alert", alerts.stream().findFirst()
                    .map(a -> Map.of(
                        "type", a.getAlertType(),
                        "severity", a.getSeverity(),
                        "detected_at", a.getDetectedAt() != null ? a.getDetectedAt().toString() : ""
                    )).orElse(Map.of())
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

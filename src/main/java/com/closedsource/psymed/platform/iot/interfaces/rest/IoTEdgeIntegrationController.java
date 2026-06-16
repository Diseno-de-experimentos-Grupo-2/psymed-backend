package com.closedsource.psymed.platform.iot.interfaces.rest;

import com.closedsource.psymed.platform.iot.domain.services.IoTCommandService;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ReceiveAlertFromEdgeResource;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ReceiveDailySummaryResource;
import com.closedsource.psymed.platform.iot.interfaces.rest.transform.ReceiveAlertCommandFromResourceAssembler;
import com.closedsource.psymed.platform.iot.interfaces.rest.transform.ReceiveDailySummaryCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/iot", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "IoT Edge Integration", description = "Endpoints called by the PsyMed Edge Server")
public class IoTEdgeIntegrationController {

    private final IoTCommandService iotCommandService;

    public IoTEdgeIntegrationController(IoTCommandService iotCommandService) {
        this.iotCommandService = iotCommandService;
    }

    @PostMapping("/alerts")
    public ResponseEntity<Map<String, Object>> receiveAlert(@RequestBody ReceiveAlertFromEdgeResource resource) {
        var command = ReceiveAlertCommandFromResourceAssembler.toCommand(resource);
        var result = iotCommandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(
            Map.of("status", "stored", "alert_id", result.get().getId(), "notification_status", "queued"),
            HttpStatus.CREATED
        );
    }

    @PostMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> receiveDailySummary(@RequestBody ReceiveDailySummaryResource resource) {
        var command = ReceiveDailySummaryCommandFromResourceAssembler.toCommand(resource);
        var result = iotCommandService.handle(command);
        if (result.isEmpty()) return ResponseEntity.badRequest().build();
        return new ResponseEntity<>(
            Map.of("status", "stored", "daily_summary_id", result.get().getId()),
            HttpStatus.CREATED
        );
    }
}

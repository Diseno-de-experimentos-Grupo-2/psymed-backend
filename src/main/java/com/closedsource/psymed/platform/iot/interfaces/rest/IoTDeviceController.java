package com.closedsource.psymed.platform.iot.interfaces.rest;

import com.closedsource.psymed.platform.iot.application.internal.IoTPatientResolver;
import com.closedsource.psymed.platform.iot.domain.model.commands.ClaimIoTDeviceCommand;
import com.closedsource.psymed.platform.iot.domain.services.IoTCommandService;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.ClaimDeviceResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/iot/devices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "IoT Devices", description = "Device pairing and lifecycle endpoints")
public class IoTDeviceController {

    private final IoTCommandService iotCommandService;
    private final IoTPatientResolver patientResolver;

    public IoTDeviceController(IoTCommandService iotCommandService, IoTPatientResolver patientResolver) {
        this.iotCommandService = iotCommandService;
        this.patientResolver = patientResolver;
    }

    @PostMapping("/claim")
    public ResponseEntity<Map<String, Object>> claimDevice(@RequestBody ClaimDeviceResource resource) {
        Long patientId = patientResolver.resolveAuthenticatedPatientId();
        var result = iotCommandService.handle(
            new ClaimIoTDeviceCommand(resource.deviceId(), resource.pairingCode(), patientId)
        );

        return switch (result.status()) {
            case SUCCESS -> ResponseEntity.ok(Map.of(
                "status", "claimed",
                "device_id", result.device().getDeviceId(),
                "patient_id", result.device().getPatientId()
            ));
            case DEVICE_NOT_FOUND -> ResponseEntity.status(404).body(Map.of("error", "device_not_found"));
            case INVALID_PAIRING_CODE -> ResponseEntity.status(400).body(Map.of("error", "invalid_pairing_code"));
            case ALREADY_CLAIMED -> ResponseEntity.status(409).body(Map.of("error", "device_already_claimed"));
            case PATIENT_ALREADY_HAS_DEVICE -> ResponseEntity.status(409).body(Map.of("error", "patient_already_has_device"));
        };
    }
}

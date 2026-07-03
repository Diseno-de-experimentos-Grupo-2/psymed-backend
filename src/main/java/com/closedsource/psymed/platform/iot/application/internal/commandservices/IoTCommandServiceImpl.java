package com.closedsource.psymed.platform.iot.application.internal.commandservices;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import com.closedsource.psymed.platform.iot.domain.model.commands.*;
import com.closedsource.psymed.platform.iot.domain.services.IoTCommandService;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.DailyHealthSummaryRepository;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.IoTAlertRepository;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.IoTDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class IoTCommandServiceImpl implements IoTCommandService {

    private final IoTDeviceRepository deviceRepository;
    private final IoTAlertRepository alertRepository;
    private final DailyHealthSummaryRepository summaryRepository;

    public IoTCommandServiceImpl(
        IoTDeviceRepository deviceRepository,
        IoTAlertRepository alertRepository,
        DailyHealthSummaryRepository summaryRepository
    ) {
        this.deviceRepository = deviceRepository;
        this.alertRepository = alertRepository;
        this.summaryRepository = summaryRepository;
    }

    @Transactional
    @Override
    public Optional<IoTDevice> handle(RegisterIoTDeviceCommand command) {
        if (deviceRepository.existsByDeviceId(command.deviceId())) {
            return deviceRepository.findByDeviceId(command.deviceId());
        }
        return Optional.of(deviceRepository.save(new IoTDevice(command)));
    }

    @Transactional
    @Override
    public Optional<IoTAlert> handle(ReceiveAlertFromEdgeCommand command) {
        if (alertRepository.existsByEdgeEventId(command.edgeEventId())) {
            return alertRepository.findByEdgeEventId(command.edgeEventId());
        }

        // The claimed device is the source of truth for the patient: ignore the
        // patient_id supplied by the Edge and resolve it from the paired device.
        var deviceOpt = deviceRepository.findByDeviceId(command.deviceId());
        if (deviceOpt.isEmpty() || !deviceOpt.get().isClaimed()) {
            return Optional.empty();
        }
        var device = deviceOpt.get();

        var resolvedCommand = new ReceiveAlertFromEdgeCommand(
            command.edgeEventId(),
            command.deviceId(),
            device.getPatientId(),
            command.alertType(),
            command.severity(),
            command.message(),
            command.detectedAt(),
            command.heartRateBpm(),
            command.temperatureC(),
            command.humidityPercent()
        );

        var alert = alertRepository.save(new IoTAlert(resolvedCommand));
        device.markSeen();
        deviceRepository.save(device);
        return Optional.of(alert);
    }

    @Transactional
    @Override
    public Optional<DailyHealthSummary> handle(ReceiveDailySummaryCommand command) {
        var deviceOpt = deviceRepository.findByDeviceId(command.deviceId());
        if (deviceOpt.isEmpty() || !deviceOpt.get().isClaimed()) {
            return Optional.empty();
        }
        var resolvedPatientId = deviceOpt.get().getPatientId();

        if (summaryRepository.existsByEdgeSummaryId(command.edgeSummaryId())) {
            return summaryRepository.findByPatientIdAndSummaryDate(resolvedPatientId, command.date());
        }

        var resolvedCommand = new ReceiveDailySummaryCommand(
            command.edgeSummaryId(),
            command.deviceId(),
            resolvedPatientId,
            command.date(),
            command.riskLevel(),
            command.summaryText(),
            command.totalReadings(),
            command.avgHeartRate(),
            command.maxHeartRate(),
            command.avgTemperatureC(),
            command.alertCount(),
            command.disconnectedMinutes(),
            command.metricsJson()
        );
        return Optional.of(summaryRepository.save(new DailyHealthSummary(resolvedCommand)));
    }

    @Transactional
    @Override
    public Optional<IoTAlert> handle(UpdateAlertStatusCommand command) {
        return alertRepository.findById(command.alertId()).map(alert -> {
            switch (command.newStatus()) {
                case "seen" -> alert.markSeen();
                case "resolved" -> alert.markResolved();
                default -> throw new IllegalArgumentException("Unknown status: " + command.newStatus());
            }
            return alertRepository.save(alert);
        });
    }

    @Transactional
    @Override
    public ClaimIoTDeviceResult handle(ClaimIoTDeviceCommand command) {
        // Code-only pairing: the patient types a single pairing code; we look up
        // the device by that code. No code match means an invalid code.
        var deviceOpt = deviceRepository.findByPairingCode(command.pairingCode());
        if (deviceOpt.isEmpty()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.INVALID_PAIRING_CODE);
        }

        var device = deviceOpt.get();
        if (device.isClaimed()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.ALREADY_CLAIMED);
        }
        if (deviceRepository.findByPatientId(command.patientId()).isPresent()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.PATIENT_ALREADY_HAS_DEVICE);
        }

        device.claim(command.patientId());
        return ClaimIoTDeviceResult.success(deviceRepository.save(device));
    }
}

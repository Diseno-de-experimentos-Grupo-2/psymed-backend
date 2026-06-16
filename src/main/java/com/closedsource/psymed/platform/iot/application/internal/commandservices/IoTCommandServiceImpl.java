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
        var alert = alertRepository.save(new IoTAlert(command));
        deviceRepository.findByDeviceId(command.deviceId()).ifPresent(d -> {
            d.markSeen();
            deviceRepository.save(d);
        });
        return Optional.of(alert);
    }

    @Transactional
    @Override
    public Optional<DailyHealthSummary> handle(ReceiveDailySummaryCommand command) {
        if (summaryRepository.existsByEdgeSummaryId(command.edgeSummaryId())) {
            return summaryRepository.findByPatientIdAndSummaryDate(command.patientId(), command.date());
        }
        return Optional.of(summaryRepository.save(new DailyHealthSummary(command)));
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
        var deviceOpt = deviceRepository.findByDeviceId(command.deviceId());
        if (deviceOpt.isEmpty()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.DEVICE_NOT_FOUND);
        }

        var device = deviceOpt.get();
        if (device.isClaimed()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.ALREADY_CLAIMED);
        }
        if (!device.matchesPairingCode(command.pairingCode())) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.INVALID_PAIRING_CODE);
        }
        if (deviceRepository.findByPatientId(command.patientId()).isPresent()) {
            return ClaimIoTDeviceResult.of(ClaimIoTDeviceResult.Status.PATIENT_ALREADY_HAS_DEVICE);
        }

        device.claim(command.patientId());
        return ClaimIoTDeviceResult.success(deviceRepository.save(device));
    }
}

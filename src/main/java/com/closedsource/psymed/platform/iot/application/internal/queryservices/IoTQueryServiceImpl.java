package com.closedsource.psymed.platform.iot.application.internal.queryservices;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import com.closedsource.psymed.platform.iot.domain.model.queries.*;
import com.closedsource.psymed.platform.iot.domain.services.IoTQueryService;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.DailyHealthSummaryRepository;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.IoTAlertRepository;
import com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories.IoTDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IoTQueryServiceImpl implements IoTQueryService {

    private final IoTDeviceRepository deviceRepository;
    private final IoTAlertRepository alertRepository;
    private final DailyHealthSummaryRepository summaryRepository;

    public IoTQueryServiceImpl(
        IoTDeviceRepository deviceRepository,
        IoTAlertRepository alertRepository,
        DailyHealthSummaryRepository summaryRepository
    ) {
        this.deviceRepository = deviceRepository;
        this.alertRepository = alertRepository;
        this.summaryRepository = summaryRepository;
    }

    @Override
    public List<IoTAlert> handle(GetAlertsByPatientIdQuery query) {
        return alertRepository.findByPatientIdOrderByDetectedAtDesc(query.patientId());
    }

    @Override
    public Optional<DailyHealthSummary> handle(GetDailySummaryByPatientAndDateQuery query) {
        return summaryRepository.findByPatientIdAndSummaryDate(query.patientId(), query.date());
    }

    @Override
    public List<DailyHealthSummary> handle(GetDailySummariesByPatientIdQuery query) {
        return summaryRepository.findByPatientIdOrderBySummaryDateDesc(query.patientId());
    }

    @Override
    public Optional<IoTDevice> handle(GetDeviceByPatientIdQuery query) {
        return deviceRepository.findByPatientId(query.patientId());
    }
}

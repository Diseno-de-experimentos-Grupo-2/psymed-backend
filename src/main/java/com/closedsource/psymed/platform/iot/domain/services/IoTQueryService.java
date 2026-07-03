package com.closedsource.psymed.platform.iot.domain.services;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import com.closedsource.psymed.platform.iot.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface IoTQueryService {
    List<IoTAlert> handle(GetAlertsByPatientIdQuery query);
    Optional<DailyHealthSummary> handle(GetDailySummaryByPatientAndDateQuery query);
    List<DailyHealthSummary> handle(GetDailySummariesByPatientIdQuery query);
    Optional<IoTDevice> handle(GetDeviceByPatientIdQuery query);
}

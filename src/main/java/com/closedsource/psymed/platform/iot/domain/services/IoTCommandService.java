package com.closedsource.psymed.platform.iot.domain.services;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;
import com.closedsource.psymed.platform.iot.domain.model.commands.*;

import java.util.Optional;

public interface IoTCommandService {
    Optional<IoTDevice> handle(RegisterIoTDeviceCommand command);
    Optional<IoTAlert> handle(ReceiveAlertFromEdgeCommand command);
    Optional<DailyHealthSummary> handle(ReceiveDailySummaryCommand command);
    Optional<IoTAlert> handle(UpdateAlertStatusCommand command);
    ClaimIoTDeviceResult handle(ClaimIoTDeviceCommand command);
}

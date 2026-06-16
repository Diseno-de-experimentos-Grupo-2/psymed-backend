package com.closedsource.psymed.platform.iot.interfaces.rest.transform;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTAlert;
import com.closedsource.psymed.platform.iot.interfaces.rest.resources.IoTAlertResource;

public class IoTAlertResourceFromEntityAssembler {
    public static IoTAlertResource toResource(IoTAlert alert) {
        return new IoTAlertResource(
            alert.getId(),
            alert.getAlertType(),
            alert.getSeverity(),
            alert.getMessage(),
            alert.getDetectedAt(),
            alert.getAlertStatus(),
            alert.getHeartRateBpm(),
            alert.getTemperatureC()
        );
    }
}

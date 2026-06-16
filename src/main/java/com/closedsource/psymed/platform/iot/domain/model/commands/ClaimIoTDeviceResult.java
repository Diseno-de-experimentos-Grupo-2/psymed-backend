package com.closedsource.psymed.platform.iot.domain.model.commands;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.IoTDevice;

public record ClaimIoTDeviceResult(Status status, IoTDevice device) {

    public enum Status {
        SUCCESS,
        DEVICE_NOT_FOUND,
        INVALID_PAIRING_CODE,
        ALREADY_CLAIMED,
        PATIENT_ALREADY_HAS_DEVICE
    }

    public static ClaimIoTDeviceResult success(IoTDevice device) {
        return new ClaimIoTDeviceResult(Status.SUCCESS, device);
    }

    public static ClaimIoTDeviceResult of(Status status) {
        return new ClaimIoTDeviceResult(status, null);
    }
}

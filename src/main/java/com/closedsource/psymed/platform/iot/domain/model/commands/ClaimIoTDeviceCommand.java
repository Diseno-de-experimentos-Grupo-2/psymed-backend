package com.closedsource.psymed.platform.iot.domain.model.commands;

public record ClaimIoTDeviceCommand(String deviceId, String pairingCode, Long patientId) {}

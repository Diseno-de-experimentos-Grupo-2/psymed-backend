package com.closedsource.psymed.platform.iot.domain.model.commands;

public record RegisterIoTDeviceCommand(String deviceId, Long patientId, String pairingCode, String edgeLocation) {}

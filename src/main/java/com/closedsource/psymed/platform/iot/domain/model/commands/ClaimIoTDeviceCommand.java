package com.closedsource.psymed.platform.iot.domain.model.commands;

public record ClaimIoTDeviceCommand(String pairingCode, Long patientId) {}

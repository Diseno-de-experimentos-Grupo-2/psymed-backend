package com.closedsource.psymed.platform.iot.domain.model.commands;

public record UpdateAlertStatusCommand(Long alertId, String newStatus) {}

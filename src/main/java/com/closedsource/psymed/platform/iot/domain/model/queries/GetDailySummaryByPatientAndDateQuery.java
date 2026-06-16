package com.closedsource.psymed.platform.iot.domain.model.queries;

import java.time.LocalDate;

public record GetDailySummaryByPatientAndDateQuery(Long patientId, LocalDate date) {}

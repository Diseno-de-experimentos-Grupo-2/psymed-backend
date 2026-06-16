package com.closedsource.psymed.platform.iot.infrastructure.persistence.jpa.repositories;

import com.closedsource.psymed.platform.iot.domain.model.aggregates.DailyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyHealthSummaryRepository extends JpaRepository<DailyHealthSummary, Long> {
    Optional<DailyHealthSummary> findByPatientIdAndSummaryDate(Long patientId, LocalDate date);
    List<DailyHealthSummary> findByPatientIdOrderBySummaryDateDesc(Long patientId);
    boolean existsByEdgeSummaryId(String edgeSummaryId);
}

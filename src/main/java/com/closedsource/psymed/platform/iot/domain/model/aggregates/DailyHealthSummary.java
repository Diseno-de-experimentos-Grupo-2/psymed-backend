package com.closedsource.psymed.platform.iot.domain.model.aggregates;

import com.closedsource.psymed.platform.iot.domain.model.commands.ReceiveDailySummaryCommand;
import com.closedsource.psymed.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Entity
@Table(
    name = "daily_health_summaries",
    uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "summary_date"})
)
public class DailyHealthSummary extends AuditableAbstractAggregateRoot<DailyHealthSummary> {

    @Column(nullable = false)
    @Getter
    private Long patientId;

    @Column(nullable = false)
    @Getter
    private LocalDate summaryDate;

    @Column(unique = true, nullable = false)
    @Getter
    private String edgeSummaryId;

    @Column(nullable = false)
    @Getter
    private String riskLevel;

    @Column(columnDefinition = "TEXT")
    @Getter
    private String summaryText;

    @Column
    @Getter
    private Integer totalReadings;

    @Column
    @Getter
    private Double avgHeartRate;

    @Column
    @Getter
    private Integer maxHeartRate;

    @Column
    @Getter
    private Double avgTemperatureC;

    @Column
    @Getter
    private Integer alertCount;

    @Column
    @Getter
    private Integer disconnectedMinutes;

    @Column(columnDefinition = "TEXT")
    @Getter
    private String metricsJson;

    public DailyHealthSummary(ReceiveDailySummaryCommand cmd) {
        this.patientId = cmd.patientId();
        this.summaryDate = cmd.date();
        this.edgeSummaryId = cmd.edgeSummaryId();
        this.riskLevel = cmd.riskLevel();
        this.summaryText = cmd.summaryText();
        this.totalReadings = cmd.totalReadings();
        this.avgHeartRate = cmd.avgHeartRate();
        this.maxHeartRate = cmd.maxHeartRate();
        this.avgTemperatureC = cmd.avgTemperatureC();
        this.alertCount = cmd.alertCount();
        this.disconnectedMinutes = cmd.disconnectedMinutes();
        this.metricsJson = cmd.metricsJson();
    }
}

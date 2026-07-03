package com.closedsource.psymed.platform.patientreport.domain.model.aggregates;

import com.closedsource.psymed.platform.patientreport.domain.model.valueobjects.MoodStatus;
import com.closedsource.psymed.platform.patientreport.domain.model.valueobjects.PatientId;
import com.closedsource.psymed.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.closedsource.psymed.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@EntityListeners(AuditableModel.class)
public class MoodState extends AuditableAbstractAggregateRoot<MoodState> {
    @NotNull
    MoodStatus moodStatus;

    @Getter
    @NotNull
    @Embedded
    private PatientId patientId;

    public MoodState() {
        this.patientId = null;
        this.moodStatus = null;
    }

    public MoodState(Long patientId, Integer moodStatus) {
        this.patientId = new PatientId(patientId);
        validateMoodStatus(moodStatus);
        switch(moodStatus) {
            case 0:
                this.moodStatus = MoodStatus.SOSAD;
                break;
            case 1:
                this.moodStatus = MoodStatus.SAD;
                break;
            case 2:
                this.moodStatus = MoodStatus.NORMAL;
                break;
            case 3:
                this.moodStatus = MoodStatus.HAPPY;
                break;
            case 4:
                this.moodStatus = MoodStatus.SOHAPPY;
                break;
            default: throw new IllegalArgumentException("Invalid mood status");
        }
    }

    private void validateMoodStatus(Integer moodStatus) {
        if(moodStatus == null || moodStatus < 0 || moodStatus > 4)
            throw new IllegalArgumentException("Invalid mood status");
    }

    public void validateRecordAvailability(MoodState lastMoodState) {
        // Mood states can be recorded once per hour (not just once per day).
        LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime lastMoodStateHour = lastMoodState.getCreatedAt().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.HOURS);
        if (Objects.equals(currentHour, lastMoodStateHour))
            throw new IllegalArgumentException("You can't record mood state twice in the same hour");
    }

    public Integer getStatus() {
        return this.moodStatus.ordinal();
    }

    public Long getLongPatientId() {
        return this.patientId.patientId();
    }


}

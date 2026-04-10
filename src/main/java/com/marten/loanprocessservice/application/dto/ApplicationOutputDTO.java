package com.marten.loanprocessservice.application.dto;

import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.application.model.RejectionReason;
import com.marten.loanprocessservice.schedule.dto.ScheduleRowOutputDTO;

import java.math.BigDecimal;
import java.util.List;

public record ApplicationOutputDTO(
        Long id,
        String firstName,
        String lastName,
        String personalCode,
        Integer loanPeriodMonths,
        BigDecimal interestMargin,
        BigDecimal baseInterestRate,
        BigDecimal loanAmount,
        ApplicationStatus status,
        RejectionReason rejectionReason,
        List<ScheduleRowOutputDTO> schedule
) {
}

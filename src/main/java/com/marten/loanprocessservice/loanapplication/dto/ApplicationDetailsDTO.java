package com.marten.loanprocessservice.loanapplication.dto;

import java.math.BigDecimal;
import java.util.List;

import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import com.marten.loanprocessservice.loanapplication.model.RejectionReason;
import com.marten.loanprocessservice.loanschedule.dto.ScheduleRowOutputDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationDetailsDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "John")
        String firstName,
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "39912310000")
        String personalCode,
        Integer loanPeriodMonths,
        BigDecimal interestMargin,
        BigDecimal baseInterestRate,
        BigDecimal loanAmount,
        ApplicationStatus status,
        @Schema(example = "null")
        RejectionReason rejectionReason,
        List<ScheduleRowOutputDTO> schedule
) {
}

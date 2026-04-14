package com.marten.loanprocessservice.loanapplication.dto;

import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import com.marten.loanprocessservice.loanapplication.model.RejectionReason;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application summary returned when the application is automatically rejected")
public record ApplicationSummaryRejectedDTO(
        Long id,
        @Schema(example = "Jane")
        String firstName,
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "39912310000")
        String personalCode,
        @Schema(example = "REJECTED")
        ApplicationStatus status,
        @Schema(example = "CUSTOMER_TOO_OLD")
        RejectionReason rejectionReason
) {
}
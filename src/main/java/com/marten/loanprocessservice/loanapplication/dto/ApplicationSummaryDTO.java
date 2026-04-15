package com.marten.loanprocessservice.loanapplication.dto;

import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import com.marten.loanprocessservice.loanapplication.model.RejectionReason;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationSummaryDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Jane")
        String firstName,
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "39912310000")
        String personalCode,
        @Schema(example = "IN_REVIEW")
        ApplicationStatus status,
        @Schema(nullable = true, example = "null")
        RejectionReason rejectionReason
        ) {

}

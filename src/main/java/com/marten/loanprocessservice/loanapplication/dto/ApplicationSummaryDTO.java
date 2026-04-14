package com.marten.loanprocessservice.loanapplication.dto;

import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import com.marten.loanprocessservice.loanapplication.model.RejectionReason;

public record ApplicationSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String personalCode,
        ApplicationStatus status,
        RejectionReason rejectionReason
) {
}

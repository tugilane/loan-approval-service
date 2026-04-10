package com.marten.loanprocessservice.application.dto;

import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.application.model.RejectionReason;

public record ApplicationSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String personalCode,
        ApplicationStatus status,
        RejectionReason rejectionReason
) {
}

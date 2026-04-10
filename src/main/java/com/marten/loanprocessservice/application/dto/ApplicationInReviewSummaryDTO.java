package com.marten.loanprocessservice.application.dto;

import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.application.model.RejectionReason;

public record ApplicationInReviewSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String personalCode
) {
}

package com.marten.loanprocessservice.loanapplication.dto;

public record ApplicationInReviewSummaryDTO(
        Long id,
        String firstName,
        String lastName,
        String personalCode
) {
}

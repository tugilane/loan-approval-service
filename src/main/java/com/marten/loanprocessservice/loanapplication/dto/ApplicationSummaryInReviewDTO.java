package com.marten.loanprocessservice.loanapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationSummaryInReviewDTO(
        Long id,
        @Schema(example = "Jane")
        String firstName,
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "39912310000")
        String personalCode
) {
}

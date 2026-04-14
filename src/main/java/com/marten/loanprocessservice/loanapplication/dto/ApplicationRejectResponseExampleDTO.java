package com.marten.loanprocessservice.loanapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Example response for rejected application")
public record ApplicationRejectResponseExampleDTO(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Jane")
        String firstName,
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "39001010005")
        String personalCode,
        @Schema(example = "REJECTED")
        String status,
        @Schema(example = "CUSTOMER_TOO_OLD")
        String rejectionReason
) {

}

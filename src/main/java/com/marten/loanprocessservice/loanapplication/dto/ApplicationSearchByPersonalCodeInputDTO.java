package com.marten.loanprocessservice.loanapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ApplicationSearchByPersonalCodeInputDTO(
        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "personal code must contain exactly 11 digits")
        @Schema(
                example = "39912310000",
                minLength = 11,
                maxLength = 11,
                description = "11-digit personal code. Birth date and final check digit must be valid."
        )
        String personalCode
        ) {

}

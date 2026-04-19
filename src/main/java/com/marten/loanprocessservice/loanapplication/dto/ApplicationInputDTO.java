package com.marten.loanprocessservice.loanapplication.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ApplicationInputDTO(
        @NotBlank
        @Size(max = 32)
        @Schema(example = "Madis", maxLength = 32)
        String firstName,
        @NotBlank
        @Size(max = 32)
        @Schema(example = "Peegel", maxLength = 32)
        String lastName,
        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "personal code must contain exactly 11 digits")
        @Schema(
                example = "39912310000",
                minLength = 11,
                maxLength = 11,
                description = "11-digit personal code. Birth date and final check digit must be valid."
        )
        String personalCode,
        @NotNull
        @Min(6)
        @Max(360)
        @Schema(example = "240", minimum = "6", maximum = "360")
        Integer loanPeriodMonths,
        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("10.0")
        @Schema(example = "2.5", minimum = "0", maximum = "99.999")
        BigDecimal interestMargin,
        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("10.0")
        @Schema(example = "4.2", minimum = "0", maximum = "99.999")
        BigDecimal baseInterestRate,
        @NotNull
        @DecimalMin("5000.0")
        @DecimalMax("9999999999999.99")
        @Schema(example = "120000", minimum = "5000", maximum = "9999999999999.99")
        BigDecimal loanAmount
) {

}

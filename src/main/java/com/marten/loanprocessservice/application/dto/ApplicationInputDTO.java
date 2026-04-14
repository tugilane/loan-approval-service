package com.marten.loanprocessservice.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ApplicationInputDTO(

        @NotBlank
        @Size(max = 32)
        String firstName,

        @NotBlank
        @Size(max = 32)
        String lastName,

        @NotNull
        @Size(min = 11, max = 11)
        String personalCode,

        @NotNull
        @Min(6)
        @Max(360)
        Integer loanPeriodMonths,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal interestMargin,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal baseInterestRate,

        @NotNull
        @DecimalMin("5000.0")
        BigDecimal loanAmount

) {}

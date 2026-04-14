package com.marten.loanprocessservice.loanapplication.dto;

import com.marten.loanprocessservice.loanapplication.model.RejectionReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RejectApplicationInputDTO(
        @NotNull
        @Schema(description = "Reason for rejecting the application", example = "CUSTOMER_TOO_OLD")
        RejectionReason rejectionReason
) {

}

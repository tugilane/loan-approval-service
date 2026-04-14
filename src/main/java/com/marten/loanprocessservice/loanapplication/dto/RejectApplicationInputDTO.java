package com.marten.loanprocessservice.loanapplication.dto;

import com.marten.loanprocessservice.loanapplication.model.RejectionReason;
import jakarta.validation.constraints.NotNull;

public record RejectApplicationInputDTO(
        @NotNull
        RejectionReason rejectionReason
) {
}

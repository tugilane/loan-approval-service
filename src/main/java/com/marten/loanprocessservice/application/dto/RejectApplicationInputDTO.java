package com.marten.loanprocessservice.application.dto;

import com.marten.loanprocessservice.application.model.RejectionReason;
import jakarta.validation.constraints.NotNull;

public record RejectApplicationInputDTO(
        @NotNull
        RejectionReason rejectionReason
) {
}

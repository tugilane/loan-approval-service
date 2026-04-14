package com.marten.loanprocessservice.loanapplication.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available rejection reasons")
public enum RejectionReason {
    CUSTOMER_TOO_OLD,
    INCOME_TOO_LOW,
    TOO_LITTLE_WORK_EXPERIENCE,
    TOO_MANY_LOANS,
    OTHER
}

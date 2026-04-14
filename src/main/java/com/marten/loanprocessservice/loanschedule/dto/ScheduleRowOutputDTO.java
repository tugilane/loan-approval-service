package com.marten.loanprocessservice.loanschedule.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduleRowOutputDTO(
        Integer paymentNumber,
        LocalDate paymentDate,
        BigDecimal monthlyPayment,
        BigDecimal principalPayment,
        BigDecimal interestPayment,
        BigDecimal remainingBalance
) {
}

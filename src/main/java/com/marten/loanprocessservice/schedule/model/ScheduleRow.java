package com.marten.loanprocessservice.schedule.model;

import com.marten.loanprocessservice.application.model.Application;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ScheduleRow {

    private Long id;

    private Integer paymentNumber;

    private LocalDate paymentDate;

    private BigDecimal monthlyPayment;

    private BigDecimal principalPayment;

    private BigDecimal interestPayment;

    private BigDecimal remainingBalance;

    private Application application;

    protected ScheduleRow() {
    }

    public ScheduleRow(Integer paymentNumber, LocalDate paymentDate, BigDecimal monthlyPayment, BigDecimal principalPayment, BigDecimal interestPayment, BigDecimal remainingBalance, Application application) {
        this.paymentNumber = paymentNumber;
        this.paymentDate = paymentDate;
        this.monthlyPayment = monthlyPayment;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
        this.remainingBalance = remainingBalance;
        this.application = application;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(Integer paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getPrincipalPayment() {
        return principalPayment;
    }

    public void setPrincipalPayment(BigDecimal principalPayment) {
        this.principalPayment = principalPayment;
    }

    public BigDecimal getInterestPayment() {
        return interestPayment;
    }

    public void setInterestPayment(BigDecimal interestPayment) {
        this.interestPayment = interestPayment;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
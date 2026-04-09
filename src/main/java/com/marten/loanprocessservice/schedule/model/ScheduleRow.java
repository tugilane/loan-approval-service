package com.marten.loanprocessservice.schedule.model;

import com.marten.loanprocessservice.application.model.Application;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_schedule")
public class ScheduleRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer paymentNumber;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalPayment;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal interestPayment;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingBalance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
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
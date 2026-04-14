package com.marten.loanprocessservice.loanapplication.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String firstName;

    @Column(nullable = false, length = 32)
    private String lastName;

    @Column(nullable = false, length = 11)
    private String personalCode;

    @Column(nullable = false)
    private Integer loanPeriodMonths;

    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal interestMargin;

    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal baseInterestRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ApplicationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 64)
    private RejectionReason rejectionReason;

    public Application() {
    }

    public Application(String firstName, String lastName, String personalCode, Integer loanPeriodMonths, BigDecimal interestMargin, BigDecimal baseInterestRate, BigDecimal loanAmount, ApplicationStatus status, RejectionReason rejectionReason) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalCode = personalCode;
        this.loanPeriodMonths = loanPeriodMonths;
        this.interestMargin = interestMargin;
        this.baseInterestRate = baseInterestRate;
        this.loanAmount = loanAmount;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalCode() {
        return personalCode;
    }

    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    public Integer getLoanPeriodMonths() {
        return loanPeriodMonths;
    }

    public void setLoanPeriodMonths(Integer loanPeriodMonths) {
        this.loanPeriodMonths = loanPeriodMonths;
    }

    public BigDecimal getInterestMargin() {
        return interestMargin;
    }

    public void setInterestMargin(BigDecimal interestMargin) {
        this.interestMargin = interestMargin;
    }

    public BigDecimal getBaseInterestRate() {
        return baseInterestRate;
    }

    public void setBaseInterestRate(BigDecimal baseInterestRate) {
        this.baseInterestRate = baseInterestRate;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

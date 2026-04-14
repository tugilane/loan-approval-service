package com.marten.loanprocessservice.loanschedule;

import com.marten.loanprocessservice.loanapplication.model.Application;
import com.marten.loanprocessservice.loanschedule.dto.ScheduleRowOutputDTO;
import com.marten.loanprocessservice.loanschedule.model.ScheduleRow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Create and save loan payment schedule for new application.
     *
     * @param application new application.
     */
    public void createAndSaveSchedule(Application application) {

        List<ScheduleRow> newSchedule = createSchedule(application);

        saveSchedule(newSchedule);
    }

    /**
     * Get loan payment schedule by already existing application (id).
     *
     * @param id application id
     * @return payment schedule
     */
    public List<ScheduleRowOutputDTO> getScheduleByApplicationId(long id) {
        return scheduleRepository.findByApplicationIdOrderByPaymentNumberAsc(id)
                .stream()
                .map(row -> new ScheduleRowOutputDTO(
                        row.getPaymentNumber(),
                        row.getPaymentDate(),
                        row.getMonthlyPayment(),
                        row.getPrincipalPayment(),
                        row.getInterestPayment(),
                        row.getRemainingBalance()
                ))
                .toList();

    }

    /**
     * Generates a loan payment schedule using annuity calculation.
     *
     * @param application application for which the schedule is for
     * @return payment schedule
     */
    private List<ScheduleRow> createSchedule(Application application) {
        BigDecimal loanAmount = application.getLoanAmount();
        int months = application.getLoanPeriodMonths();

        // add interest margin and base interest rate to get total annual interest rate
        BigDecimal annualRatePercent = application.getInterestMargin()
                .add(application.getBaseInterestRate());

        // now we get the monthly interest rate.
        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP) // convert percentage to decimal
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP); // convert annual to monthly

        LocalDate firstPaymentDate = LocalDate.now();

        List<ScheduleRow> schedule = new ArrayList<>();

        BigDecimal monthlyPayment;
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) { // if interest is 0%, then just divide by months
            monthlyPayment = loanAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        } else { // else we need to calculate the annuity payment
            double r = monthlyRate.doubleValue();
            double p = loanAmount.doubleValue();
            int n = months;

            // M = P * (r(1 + r)^n) / ((1 + r)^n - 1)
            double annuity = p * (r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
            monthlyPayment = BigDecimal.valueOf(annuity).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal remainingBalance = loanAmount;

        // loop over all months
        for (int i = 1; i <= months; i++) {
            // calculate interest payment for every month
            BigDecimal interestPayment = remainingBalance
                    .multiply(monthlyRate);

            // calculate principal payment for every month
            BigDecimal principalPayment = monthlyPayment
                    .subtract(interestPayment);

            // final month payment, to fix possible loan monthly payment rounding error.
            // In some cases the last month might add or remove some small calculation remainder to the payment.
            if (i == months) {
                principalPayment = remainingBalance;
                monthlyPayment = principalPayment.add(interestPayment);
            }

            remainingBalance = remainingBalance
                    .subtract(principalPayment);

            ScheduleRow row = new ScheduleRow(
                    i,
                    firstPaymentDate.plusMonths(i - 1L),
                    monthlyPayment.setScale(2, RoundingMode.HALF_UP),
                    principalPayment.setScale(2, RoundingMode.HALF_UP),
                    interestPayment.setScale(2, RoundingMode.HALF_UP),
                    remainingBalance.setScale(2, RoundingMode.HALF_UP),
                    application
            );

            schedule.add(row);
        }

/*        for (int i = 0; i < schedule.size(); i++) {
            System.out.println(schedule.get(i).getPaymentNumber() + " " + schedule.get(i).getPaymentDate() + " " + schedule.get(i).getMonthlyPayment() + " " + schedule.get(i).getPrincipalPayment() + " " + schedule.get(i).getInterestPayment() + " " + schedule.get(i).getRemainingBalance());
        }*/

        return schedule;
    }

    private void saveSchedule(List<ScheduleRow> schedule) {
        scheduleRepository.saveAll(schedule);
    }
}

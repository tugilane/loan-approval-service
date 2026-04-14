package com.marten.loanprocessservice.schedule;

import com.marten.loanprocessservice.application.model.Application;
import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.schedule.dto.ScheduleRowOutputDTO;
import com.marten.loanprocessservice.schedule.model.ScheduleRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    void createAndSaveScheduleWithZeroInterestCreatesFlatPayments() {
        Application application = createApplication(12, new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("12000.00"));

        List<ScheduleRow> schedule = invokeCreateSchedule(application);

        invokeSaveSchedule(schedule);
        verify(scheduleRepository).saveAll(same(schedule));

        assertEquals(12, schedule.size());
        assertEquals(1, schedule.getFirst().getPaymentNumber());
        assertEquals(LocalDate.now(), schedule.getFirst().getPaymentDate());
        assertEquals(new BigDecimal("1000.00"), schedule.getFirst().getMonthlyPayment());
        assertEquals(new BigDecimal("1000.00"), schedule.getFirst().getPrincipalPayment());
        assertEquals(new BigDecimal("0.00"), schedule.getFirst().getInterestPayment());

        ScheduleRow last = schedule.getLast();
        assertEquals(12, last.getPaymentNumber());
        assertEquals(new BigDecimal("0.00"), last.getRemainingBalance());
    }

    @Test
    void createAndSaveScheduleWithInterestCreatesAnnuityRows() {
        Application application = createApplication(24, new BigDecimal("1.50"), new BigDecimal("2.00"), new BigDecimal("10000.00"));

        List<ScheduleRow> schedule = invokeCreateSchedule(application);

        invokeSaveSchedule(schedule);
        verify(scheduleRepository).saveAll(same(schedule));

        assertEquals(24, schedule.size());
        ScheduleRow first = schedule.getFirst();
        assertTrue(first.getInterestPayment().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(first.getMonthlyPayment().compareTo(first.getPrincipalPayment()) > 0);

        ScheduleRow last = schedule.getLast();
        assertEquals(new BigDecimal("0.00"), last.getRemainingBalance());
        assertTrue(last.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void getScheduleByApplicationIdMapsRowsToOutputDto() {
        Application application = createApplication(12, new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("12000.00"));
        List<ScheduleRow> rows = List.of(
                new ScheduleRow(
                        1,
                        LocalDate.of(2026, 1, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("1000.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("11000.00"),
                        application
                ),
                new ScheduleRow(
                        2,
                        LocalDate.of(2026, 2, 1),
                        new BigDecimal("1000.00"),
                        new BigDecimal("1000.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("10000.00"),
                        application
                )
        );

        when(scheduleRepository.findByApplicationIdOrderByPaymentNumberAsc(7L)).thenReturn(rows);

        List<ScheduleRowOutputDTO> result = scheduleService.getScheduleByApplicationId(7L);

        assertEquals(2, result.size());
        assertEquals(1, result.getFirst().paymentNumber());
        assertEquals(LocalDate.of(2026, 1, 1), result.getFirst().paymentDate());
        assertEquals(new BigDecimal("1000.00"), result.getFirst().monthlyPayment());
        assertEquals(new BigDecimal("10000.00"), result.get(1).remainingBalance());
    }

    @SuppressWarnings("unchecked")
    private List<ScheduleRow> invokeCreateSchedule(Application application) {
        return (List<ScheduleRow>) ReflectionTestUtils.invokeMethod(scheduleService, "createSchedule", application);
    }

    private void invokeSaveSchedule(List<ScheduleRow> schedule) {
        ReflectionTestUtils.invokeMethod(scheduleService, "saveSchedule", schedule);
    }

    private static Application createApplication(
            int months,
            BigDecimal interestMargin,
            BigDecimal baseInterestRate,
            BigDecimal loanAmount
    ) {
        return new Application(
                "John",
                "Doe",
                "39912310000",
                months,
                interestMargin,
                baseInterestRate,
                loanAmount,
                ApplicationStatus.IN_REVIEW,
                null
        );
    }
}
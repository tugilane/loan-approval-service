package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.dto.ApplicationDetailsDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInReviewSummaryDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInputDTO;
import com.marten.loanprocessservice.application.dto.ApplicationSummaryDTO;
import com.marten.loanprocessservice.application.dto.RejectApplicationInputDTO;
import com.marten.loanprocessservice.application.model.Application;
import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.application.model.RejectionReason;
import com.marten.loanprocessservice.schedule.ScheduleService;
import com.marten.loanprocessservice.schedule.dto.ScheduleRowOutputDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(applicationService, "maxAge", 65);
    }

    @Test
    void processApplicationSavesInReviewAndCreatesSchedule() {
        ApplicationInputDTO input = new ApplicationInputDTO(
                "John",
                "Doe",
                "39912310000",
                24,
                new BigDecimal("1.50"),
                new BigDecimal("2.00"),
                new BigDecimal("10000.00")
        );

        when(applicationRepository.existsByPersonalCodeAndStatus(input.personalCode(), ApplicationStatus.IN_REVIEW))
                .thenReturn(false);

        applicationService.processApplication(input);

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository, times(1)).save(applicationCaptor.capture());
        Application saved = applicationCaptor.getValue();

        assertEquals(ApplicationStatus.IN_REVIEW, saved.getStatus());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals(input.loanAmount(), saved.getLoanAmount());
    }

    @Test
    void processApplicationRejectsTooOldApplicant() {
        ApplicationInputDTO input = new ApplicationInputDTO(
                "Old",
                "Person",
                "30001010004",
                12,
                new BigDecimal("1.00"),
                new BigDecimal("1.00"),
                new BigDecimal("7000.00")
        );

        when(applicationRepository.existsByPersonalCodeAndStatus(input.personalCode(), ApplicationStatus.IN_REVIEW))
                .thenReturn(false);

        applicationService.processApplication(input);

        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(applicationCaptor.capture());
        Application saved = applicationCaptor.getValue();

        assertEquals(ApplicationStatus.REJECTED, saved.getStatus());
        assertEquals(RejectionReason.CUSTOMER_TOO_OLD, saved.getRejectionReason());
    }

    @Test
    void processApplicationThrowsWhenActiveApplicationAlreadyExists() {
        ApplicationInputDTO input = new ApplicationInputDTO(
                "Jane",
                "Doe",
                "39912310000",
                18,
                new BigDecimal("1.20"),
                new BigDecimal("2.30"),
                new BigDecimal("12000.00")
        );

        when(applicationRepository.existsByPersonalCodeAndStatus(input.personalCode(), ApplicationStatus.IN_REVIEW))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.processApplication(input)
        );

        assertTrue(ex.getMessage().contains("active application"));
        verify(applicationRepository, never()).save(any());
        verifyNoInteractions(scheduleService);
    }

    @Test
    void processApplicationThrowsOnFutureBirthDate() {
        ApplicationInputDTO input = new ApplicationInputDTO(
                "Future",
                "Person",
                "55001010001",
                12,
                new BigDecimal("1.00"),
                new BigDecimal("1.00"),
                new BigDecimal("9000.00")
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.processApplication(input)
        );

        assertTrue(ex.getMessage().contains("future"));
        verify(applicationRepository, never()).save(any());
        verifyNoInteractions(scheduleService);
    }

    @Test
    void processApplicationThrowsOnInvalidCheckNumber() {
        ApplicationInputDTO input = new ApplicationInputDTO(
                "Wrong",
                "Check",
                "39912310001",
                12,
                new BigDecimal("1.00"),
                new BigDecimal("1.00"),
                new BigDecimal("9000.00")
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.processApplication(input)
        );

        assertTrue(ex.getMessage().contains("check number"));
        verify(applicationRepository, never()).existsByPersonalCodeAndStatus(any(), any());
        verify(applicationRepository, never()).save(any());
        verifyNoInteractions(scheduleService);
    }

    @Test
    void getApplicationDetailsReturnsApplicationAndSchedule() {
        Application application = baseApplication(1L, ApplicationStatus.IN_REVIEW);
        List<ScheduleRowOutputDTO> schedule = List.of(
                new ScheduleRowOutputDTO(
                        1,
                        java.time.LocalDate.now(),
                        new BigDecimal("450.00"),
                        new BigDecimal("400.00"),
                        new BigDecimal("50.00"),
                        new BigDecimal("9600.00")
                )
        );

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(scheduleService.getScheduleByApplicationId(1L)).thenReturn(schedule);

        ApplicationDetailsDTO result = applicationService.getApplicationDetails(1L);

        assertEquals(1L, result.id());
        assertEquals("John", result.firstName());
        assertEquals(ApplicationStatus.IN_REVIEW, result.status());
        assertEquals(1, result.schedule().size());
        assertEquals(new BigDecimal("450.00"), result.schedule().getFirst().monthlyPayment());
    }

    @Test
    void getAllApplicationsMapsEntitiesToSummaryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Application application = baseApplication(2L, ApplicationStatus.REJECTED);
        application.setRejectionReason(RejectionReason.OTHER);
        Page<Application> page = new PageImpl<>(List.of(application), pageable, 1);

        when(applicationRepository.findAll(pageable)).thenReturn(page);

        Page<ApplicationSummaryDTO> result = applicationService.getAllApplications(pageable);

        assertEquals(1, result.getTotalElements());
        ApplicationSummaryDTO summary = result.getContent().getFirst();
        assertEquals(2L, summary.id());
        assertEquals(ApplicationStatus.REJECTED, summary.status());
        assertEquals(RejectionReason.OTHER, summary.rejectionReason());
    }

    @Test
    void getAllApplicationsInReviewMapsEntitiesToInReviewSummaryDto() {
        Pageable pageable = PageRequest.of(0, 5);
        Application application = baseApplication(3L, ApplicationStatus.IN_REVIEW);
        Page<Application> page = new PageImpl<>(List.of(application), pageable, 1);

        when(applicationRepository.findByStatus(ApplicationStatus.IN_REVIEW, pageable)).thenReturn(page);

        Page<ApplicationInReviewSummaryDTO> result = applicationService.getAllApplicationsInReview(pageable);

        assertEquals(1, result.getTotalElements());
        ApplicationInReviewSummaryDTO summary = result.getContent().getFirst();
        assertEquals(3L, summary.id());
        assertEquals("John", summary.firstName());
    }

    @Test
    void approveApplicationChangesStatusToApproved() {
        Application application = baseApplication(4L, ApplicationStatus.IN_REVIEW);
        when(applicationRepository.findById(4L)).thenReturn(Optional.of(application));

        applicationService.approveApplication(4L);

        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        verify(applicationRepository).save(application);
    }

    @Test
    void approveApplicationThrowsWhenStatusIsNotInReview() {
        Application application = baseApplication(4L, ApplicationStatus.REJECTED);
        when(applicationRepository.findById(4L)).thenReturn(Optional.of(application));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> applicationService.approveApplication(4L)
        );

        assertTrue(ex.getMessage().contains("in review"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void rejectApplicationChangesStatusAndSetsReason() {
        Application application = baseApplication(5L, ApplicationStatus.IN_REVIEW);
        when(applicationRepository.findById(5L)).thenReturn(Optional.of(application));

        applicationService.rejectApplication(5L, new RejectApplicationInputDTO(RejectionReason.TOO_MANY_LOANS));

        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertEquals(RejectionReason.TOO_MANY_LOANS, application.getRejectionReason());
        verify(applicationRepository).save(application);
    }

    @Test
    void rejectApplicationThrowsWhenStatusIsNotInReview() {
        Application application = baseApplication(6L, ApplicationStatus.APPROVED);
        when(applicationRepository.findById(6L)).thenReturn(Optional.of(application));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.rejectApplication(6L, new RejectApplicationInputDTO(RejectionReason.OTHER))
        );

        assertTrue(ex.getMessage().contains("in review"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void getApplicationDetailsThrowsWhenApplicationNotFound() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.getApplicationDetails(999L)
        );

        assertEquals("Application not found", ex.getMessage());
        verify(scheduleService, never()).getScheduleByApplicationId(any(Long.class));
    }

    private static Application baseApplication(Long id, ApplicationStatus status) {
        Application application = new Application(
                "John",
                "Doe",
                "39912310000",
                24,
                new BigDecimal("1.50"),
                new BigDecimal("2.00"),
                new BigDecimal("10000.00"),
                status,
                null
        );
        application.setId(id);
        assertNotNull(application.getId());
        return application;
    }
}
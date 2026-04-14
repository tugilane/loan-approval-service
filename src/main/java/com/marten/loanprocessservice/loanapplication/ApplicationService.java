package com.marten.loanprocessservice.loanapplication;

import com.marten.loanprocessservice.loanapplication.dto.*;
import com.marten.loanprocessservice.loanapplication.model.Application;
import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import com.marten.loanprocessservice.loanapplication.model.RejectionReason;
import com.marten.loanprocessservice.loanschedule.ScheduleService;
import com.marten.loanprocessservice.loanschedule.dto.ScheduleRowOutputDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ScheduleService scheduleService;

    @Value("${loan.max-age}")
    private int maxAge;

    public ApplicationService(ApplicationRepository applicationRepository, ScheduleService scheduleService) {
        this.applicationRepository = applicationRepository;
        this.scheduleService = scheduleService;
    }

    /**
     * Processes a new loan application.
     * Validates the applicant's personal code, ensures that no application is currently in review.
     * Creates the application and persists it. A payment schedule is created only for applications in review.
     *
     * @param dto - validated application input data
     */
    @Transactional // application and schedule go hand-in-hand, if creating one fails, the other should too.
    public ApplicationSummaryDTO processApplication(ApplicationInputDTO dto) {

        // validate the personal code
        LocalDate birthdate = getAndValidateBirthdate(dto.personalCode());

        // validate the check number of the personal code
        validateCheckNumer(dto.personalCode());

        // throw exception if this personal code already has application in review
        checkIfApplicationExistsByStatus(dto.personalCode(), ApplicationStatus.IN_REVIEW);

        Application newApplication = createApplication(dto, birthdate);

        saveApplication(newApplication);

        // No schedule is needed for auto-rejected applications.
        if (newApplication.getStatus() == ApplicationStatus.IN_REVIEW) {
            scheduleService.createAndSaveSchedule(newApplication);
        }

        return toSummaryDTO(newApplication);
    }

    /**
     * Returns detailed information for a single application, including its schedule.
     *
     * @param id application id
     * @return application details and the corresponding schedule
     */
    public ApplicationDetailsDTO getApplicationDetails(long id) {

        Application application = findApplicationById(id);

        // ask schedule service for the schedule of this application
        List<ScheduleRowOutputDTO> schedule = scheduleService.getScheduleByApplicationId(id);

        return new ApplicationDetailsDTO(
                application.getId(),
                application.getFirstName(),
                application.getLastName(),
                application.getPersonalCode(),
                application.getLoanPeriodMonths(),
                application.getInterestMargin(),
                application.getBaseInterestRate(),
                application.getLoanAmount(),
                application.getStatus(),
                application.getRejectionReason(),
                schedule
        );
    }

    /**
     * Returns a paginated list of application summaries.
     *
     * @param pageable application id
     * @return paginated application summaries
     */
    public Page<ApplicationSummaryDTO> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable)
                .map(application -> new ApplicationSummaryDTO(
                        application.getId(),
                        application.getFirstName(),
                        application.getLastName(),
                        application.getPersonalCode(),
                        application.getStatus(),
                        application.getRejectionReason()
                ));
    }

    /**
     * Returns a paginated list of application summaries that are in review.
     *
     * @param pageable application id
     * @return paginated application summaries (in-review)
     */
    public Page<ApplicationSummaryInReviewDTO> getAllApplicationsInReview(Pageable pageable) {
        return applicationRepository.findByStatus(ApplicationStatus.IN_REVIEW, pageable)
                .map(application -> new ApplicationSummaryInReviewDTO(
                        application.getId(),
                        application.getFirstName(),
                        application.getLastName(),
                        application.getPersonalCode()
                ));
    }

    /**
     * Approves an application that is currently in review.
     *
     * @param id application id
     */
    public ApplicationSummaryDTO approveApplication(long id) {
        Application application = findApplicationById(id);

        if (application.getStatus() != ApplicationStatus.IN_REVIEW) {
            throw new IllegalStateException("Only applications in review can be approved");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        saveApplication(application);

        return toSummaryDTO(application);

    }

    /**
     * Rejects an application that is currently in review.
     *
     * @param id application id
     */
    public ApplicationSummaryDTO rejectApplication(long id, RejectApplicationInputDTO dto) {
        Application application = findApplicationById(id);

        if (application.getStatus() != ApplicationStatus.IN_REVIEW) {
            throw new IllegalArgumentException("Only applications in review can be rejected");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectionReason(dto.rejectionReason());

        saveApplication(application);

        return toSummaryDTO(application);
    }

    /**
     * Validate and return the birthdate of the applicant based on the personal code.
     *
     * @param personalCode personal code of the applicant
     * @return birthdate of the applicant.
     */
    private LocalDate getAndValidateBirthdate(String personalCode) {
        int firstDigit = Character.getNumericValue(personalCode.charAt(0));

        int year = Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));

        // first digit must be between 1 and 8
        int century;
        if (firstDigit == 1 || firstDigit == 2) century = 1800;
        else if (firstDigit == 3 || firstDigit == 4) century = 1900;
        else if (firstDigit == 5 || firstDigit == 6) century = 2000;
        else if (firstDigit == 7 || firstDigit == 8) century = 2100;
        else throw new IllegalArgumentException("Invalid personal code: first digit must be between 1 and 8");

        int fullYear = century + year;

        LocalDate birthDate;
        try {
            birthDate = LocalDate.of(fullYear, month, day); // validate birthdate

            if (birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Invalid personal code: birth date in the future");
            }

        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid personal code: invalid birth date");
        }
        return birthDate;
    }

    /**
     * Validate check number of the applicatant's personal code.
     *
     * @param personalCode personal code of the applicant
     */
    private void validateCheckNumer(String personalCode) {
        int[] weights1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        int[] weights2 = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};

        // The first 10 digits of the personal code are multiplied
        // by the corresponding first-level weights and summed up.
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(personalCode.charAt(i)) * weights1[i];
        }

        // The sum is divided by 11.
        int remainder = sum % 11;

        // If the remainder is less than 10, it is used as the check number.
        int checkNum;
        if (remainder < 10) {
            checkNum = remainder;

            // If the remainder equals 10,
            // the calculation is repeated using second-level weights.
        } else {
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(personalCode.charAt(i)) * weights2[i];
            }
            // The new sum is divided by 11.
            // If the remainder is less than 10, it becomes the check number.
            // If the remainder is 10, the control check is set to 0.
            remainder = sum % 11;
            checkNum = (remainder < 10) ? remainder : 0;
        }

        int lastDigit = Character.getNumericValue(personalCode.charAt(10));

        if (checkNum != lastDigit) { // validate check number
            throw new IllegalArgumentException("Invalid personal code: check number does not match");
        }
    }

    /**
     * Create and return a new application
     *
     * @param dto       application input data from controller
     * @param birthDate applicant birthdate
     * @return application entity
     */
    private Application createApplication(ApplicationInputDTO dto, LocalDate birthDate) {

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        ApplicationStatus status;
        RejectionReason rejectionReason = null;

        // check if too old
        if (age > maxAge) {
            status = ApplicationStatus.REJECTED;
            rejectionReason = RejectionReason.CUSTOMER_TOO_OLD;
        } else {
            status = ApplicationStatus.IN_REVIEW;
        }

        Application application = new Application(
                dto.firstName(),
                dto.lastName(),
                dto.personalCode(),
                dto.loanPeriodMonths(),
                dto.interestMargin(),
                dto.baseInterestRate(),
                dto.loanAmount(),
                status,
                rejectionReason
        );

/*        System.out.println("Application created: " + application);
        System.out.println("Age of applicant: " + age);
        System.out.println("Application status: " + status);*/

        return application;
    }

    private void saveApplication(Application application) {
        applicationRepository.save(application);
    }

    private Application findApplicationById(long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }

    private void checkIfApplicationExistsByStatus(String personalCode, ApplicationStatus status) {
        if (applicationRepository.existsByPersonalCodeAndStatus(personalCode, status)) {
            throw new IllegalArgumentException("Customer already has an active application");
        }
    }

    private ApplicationSummaryDTO toSummaryDTO(Application application) {
        return new ApplicationSummaryDTO(
                application.getId(),
                application.getFirstName(),
                application.getLastName(),
                application.getPersonalCode(),
                application.getStatus(),
                application.getRejectionReason()
        );
    }

}

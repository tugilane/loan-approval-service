package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.dto.ApplicationDetailsDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInReviewSummaryDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInputDTO;
import com.marten.loanprocessservice.application.dto.ApplicationSummaryDTO;
import com.marten.loanprocessservice.application.model.Application;
import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.application.model.RejectionReason;
import com.marten.loanprocessservice.schedule.ScheduleService;
import com.marten.loanprocessservice.schedule.dto.ScheduleRowOutputDTO;
import com.marten.loanprocessservice.schedule.model.ScheduleRow;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ScheduleService scheduleService;

    public ApplicationService(ApplicationRepository applicationRepository, ScheduleService scheduleService) {
        this.applicationRepository = applicationRepository;
        this.scheduleService = scheduleService;
    }

    @Value("${loan.max-age}")
    private int maxAge;

    @Transactional // appointment and schedule go hand-in-hand, if one fails, the other should too.
    public void processApplication(ApplicationInputDTO dto) {

        // validate the personal code
        LocalDate birthdate = getAndValidateBirthdate(dto.personalCode());
        validateCheckNumer(dto.personalCode());

        if (applicationRepository.existsByPersonalCodeAndStatus(dto.personalCode(), ApplicationStatus.IN_REVIEW)) {
            throw new IllegalArgumentException("Customer already has an active application");
        }

        createApplication(dto, birthdate);
    }

    public void createApplication(ApplicationInputDTO dto, LocalDate birthDate) {

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        ApplicationStatus status;
        RejectionReason rejectionReason = null;

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

        applicationRepository.save(application);

        List<ScheduleRow> schedule = scheduleService.createSchedule(application);
        scheduleService.saveSchedule(schedule);
    }

    // check if personal code is correct format, later on in other methods we check if the age is acceptable
    public LocalDate getAndValidateBirthdate(String personalCode) {
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
            birthDate = LocalDate.of(fullYear, month, day); // the day and month validation check

            if (birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Invalid personal code: birth date in the future");
            }

        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid personal code: invalid birth date");
        }
        return birthDate;
    }

    public void validateCheckNumer(String personalCode) {
        int[] weights1 = {1,2,3,4,5,6,7,8,9,1};
        int[] weights2 = {3,4,5,6,7,8,9,1,2,3};

        // The first 10 digits of the personal code are multiplied
        // by the corresponding first-level weights and summed up.
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(personalCode.charAt(i)) * weights1[i];
        }

        // The sum is divided by 11.
        int remainder = sum % 11;

        // If the remainder is less than 10, it is used as the control number.
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
            // If the remainder is less than 10, it becomes the control number.
            // If the remainder is 10, the control number is set to 0.
            remainder = sum % 11;
            checkNum = (remainder < 10) ? remainder : 0;
        }

        int lastDigit = Character.getNumericValue(personalCode.charAt(10));

        if (checkNum != lastDigit) {
            throw new IllegalArgumentException("Invalid personal code: check number does not match");
        }
    }

    public ApplicationDetailsDTO getApplicationDetails(long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

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

    public List<ApplicationSummaryDTO> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(application -> new ApplicationSummaryDTO(
                        application.getId(),
                        application.getFirstName(),
                        application.getLastName(),
                        application.getPersonalCode(),
                        application.getStatus(),
                        application.getRejectionReason()
                ))
                .toList();
    }

    public List<ApplicationInReviewSummaryDTO> getAllApplicationsInReview() {
        return applicationRepository.findByStatus(ApplicationStatus.IN_REVIEW)
                .stream()
                .map(application -> new ApplicationInReviewSummaryDTO(
                        application.getId(),
                        application.getFirstName(),
                        application.getLastName(),
                        application.getPersonalCode()
                ))
                .toList();
    }
}

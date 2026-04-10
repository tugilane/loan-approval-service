package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.dto.ApplicationDetailsDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInReviewSummaryDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInputDTO;
import com.marten.loanprocessservice.application.dto.ApplicationSummaryDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> receiveLoanApplication(@Valid @RequestBody ApplicationInputDTO dto) {
        applicationService.processApplication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationDetailsDTO> getApplication(@PathVariable long id){
        return ResponseEntity.ok(applicationService.getApplicationDetails(id));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationSummaryDTO>> getAllApplications(){
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/applications/in-review")
    public ResponseEntity<List<ApplicationInReviewSummaryDTO>> getAllApplicationsInReview(){
        return ResponseEntity.ok(applicationService.getAllApplicationsInReview());
    }
}

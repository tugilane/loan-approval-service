package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<ApplicationSummaryDTO>> getAllApplications(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.ok(applicationService.getAllApplications(pageable));
    }

    @GetMapping("/applications/in-review")
    public ResponseEntity<Page<ApplicationInReviewSummaryDTO>> getAllApplicationsInReview(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.ok(applicationService.getAllApplicationsInReview(pageable));
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<Void> approveApplication(@PathVariable long id){
        return ResponseEntity.ok(applicationService.approveApplication(id));
}

    @PostMapping("applications/{id}/reject")
    public ResponseEntity<Void> rejectApplication(@PathVariable long id, @Valid @RequestBody RejectApplicationInputDTO dto) {
        applicationService.rejectApplication(id, dto);
        return ResponseEntity.ok().build();
    }
}

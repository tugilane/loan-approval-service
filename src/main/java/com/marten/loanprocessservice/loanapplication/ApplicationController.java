package com.marten.loanprocessservice.loanapplication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marten.loanprocessservice.loanapplication.dto.ApplicationApproveResponseExampleDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationDetailsDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationSummaryInReviewDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationInputDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationRejectResponseExampleDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationSummaryDTO;
import com.marten.loanprocessservice.loanapplication.dto.ApplicationSummaryRejectedDTO;
import com.marten.loanprocessservice.loanapplication.dto.RejectApplicationInputDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Loan Application processing")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Submit a new loan application", description = "Personal code must have a valid birth date and check number. Applications can be automatically rejected if the applicant is too old.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created",
                    content = @Content(schema = @Schema(oneOf = {
                            ApplicationSummaryDTO.class,
                            ApplicationSummaryRejectedDTO.class
                    })))
    })
    public ResponseEntity<ApplicationSummaryDTO> receiveLoanApplication(@Valid @RequestBody ApplicationInputDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.processApplication(dto));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get one application with schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application details",
                    content = @Content(schema = @Schema(implementation = ApplicationDetailsDTO.class)))
    })
    public ResponseEntity<ApplicationDetailsDTO> getApplication(@Parameter(description = "Application id") @PathVariable long id) {
        return ResponseEntity.ok(applicationService.getApplicationDetails(id));
    }

    @GetMapping("/applications")
    @Operation(summary = "List all applications", description = "Supports standard paging and sorting parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged application list")
    })
    public ResponseEntity<Page<ApplicationSummaryDTO>> getAllApplications(
            @Parameter(description = "page index", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(applicationService.getAllApplications(pageable));
    }

    @GetMapping("/applications/in-review")
    @Operation(summary = "List applications in review", description = "Supports standard paging and sorting parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged in-review application list")
    })
    public ResponseEntity<Page<ApplicationSummaryInReviewDTO>> getAllApplicationsInReview(
            @Parameter(description = "page index", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction", example = "DESC") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(applicationService.getAllApplicationsInReview(pageable));
    }

    @PostMapping("/applications/{id}/approve")
    @Operation(summary = "Approve an in-review application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application approved",
                    content = @Content(schema = @Schema(implementation = ApplicationApproveResponseExampleDTO.class)))
    })
    public ResponseEntity<ApplicationSummaryDTO> approveApplication(@Parameter(description = "Application id") @PathVariable long id) {
        return ResponseEntity.ok(applicationService.approveApplication(id));
    }

    @PostMapping("/applications/{id}/reject")
    @Operation(summary = "Reject an in-review application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application rejected",
                    content = @Content(schema = @Schema(implementation = ApplicationRejectResponseExampleDTO.class)))
    })
    public ResponseEntity<ApplicationSummaryDTO> rejectApplication(@Parameter(description = "Application id") @PathVariable long id, @Valid @RequestBody RejectApplicationInputDTO dto) {
        return ResponseEntity.ok(applicationService.rejectApplication(id, dto));
    }
}

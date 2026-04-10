package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.dto.ApplicationOutputDTO;
import com.marten.loanprocessservice.application.dto.ApplicationInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApplicationOutputDTO> getApplication(@PathVariable long id){
        return ResponseEntity.ok(applicationService.getApplicationDetails(id));
    }

    //@GetMapping("/applications/show/inreview")
}

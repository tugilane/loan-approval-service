package com.marten.loanprocessservice.loanapplication;

import com.marten.loanprocessservice.loanapplication.model.Application;
import com.marten.loanprocessservice.loanapplication.model.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByPersonalCodeAndStatus(String personalCode, ApplicationStatus status);

    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);
}

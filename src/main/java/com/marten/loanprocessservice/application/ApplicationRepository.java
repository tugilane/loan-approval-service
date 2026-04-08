package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.model.Application;
import com.marten.loanprocessservice.application.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByPersonalCodeAndStatus(String personalCode, ApplicationStatus status);
}

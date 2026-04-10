package com.marten.loanprocessservice.application;

import com.marten.loanprocessservice.application.model.Application;
import com.marten.loanprocessservice.application.model.ApplicationStatus;
import com.marten.loanprocessservice.schedule.model.ScheduleRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByPersonalCodeAndStatus(String personalCode, ApplicationStatus status);

    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);
}

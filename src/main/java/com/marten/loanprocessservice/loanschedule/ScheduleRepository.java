package com.marten.loanprocessservice.loanschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import com.marten.loanprocessservice.loanschedule.model.ScheduleRow;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleRow, Long> {
    List<ScheduleRow> findByApplicationIdOrderByPaymentNumberAsc(Long applicationId);
}

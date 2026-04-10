package com.marten.loanprocessservice.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import com.marten.loanprocessservice.schedule.model.ScheduleRow;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleRow, Long> {
    List<ScheduleRow> findByApplicationIdOrderByPaymentNumberAsc(Long applicationId);
}

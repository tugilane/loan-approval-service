package com.marten.loanprocessservice.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import com.marten.loanprocessservice.schedule.model.ScheduleRow;

public interface ScheduleRepository extends JpaRepository<ScheduleRow, Long> {
}

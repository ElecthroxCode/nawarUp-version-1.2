package com.nawarup.api.catalog.repository;

import com.nawarup.api.catalog.model.BusinessSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface BusinessScheduleRepository extends JpaRepository<BusinessSchedule, Long> {
    List<BusinessSchedule> findByBusinessIdOrderByDayOfWeek(Long businessId);
    Optional<BusinessSchedule> findByBusinessIdAndDayOfWeek(Long businessId, DayOfWeek dayOfWeek);
    void deleteByBusinessId(Long businessId);
}

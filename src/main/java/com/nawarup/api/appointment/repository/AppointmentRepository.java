package com.nawarup.api.appointment.repository;

import com.nawarup.api.appointment.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"business", "customer", "details", "details.service"})
    List<Appointment> findByCustomerIdOrderByStartTimeDesc(Long customerId);

    @EntityGraph(attributePaths = {"business", "customer", "details", "details.service"})
    List<Appointment> findByBusinessIdOrderByStartTimeDesc(Long businessId);

    /** ¿Existe una cita activa que se solape con [start, end)? */
    @Query("""
           select count(a) > 0 from Appointment a
           where a.business.id = :businessId
             and a.status <> com.nawarup.api.appointment.model.AppointmentStatus.CANCELLED
             and a.startTime < :end and a.endTime > :start
           """)
    boolean existsOverlap(@Param("businessId") Long businessId,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    @Query("""
           select a from Appointment a
           where a.business.id = :businessId
             and a.status <> com.nawarup.api.appointment.model.AppointmentStatus.CANCELLED
             and a.startTime >= :from and a.startTime < :to
           order by a.startTime
           """)
    List<Appointment> findActiveBetween(@Param("businessId") Long businessId,
                                        @Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}

package com.example.demo.repository;

import com.example.demo.model.entity.QEnergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QEnergyRepository extends JpaRepository<QEnergy, Long> {

    @Query("SELECT q FROM QEnergy q WHERE q.space.spaceId = :spaceId " +
            "AND q.date BETWEEN :start AND :end")
    List<QEnergy> findBySpaceAndDateRange(@Param("spaceId") Integer spaceId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT q FROM QEnergy q " +
            "WHERE q.space.spaceId = :spaceId " +
            "AND q.date >= :startDateTime " +
            "AND q.date < :endDateTime " +
            "ORDER BY q.date ASC")
    List<QEnergy> findBySpaceAndDateBetween(
            @Param("spaceId") Integer spaceId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}

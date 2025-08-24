package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.entity.tree.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    // sửa lại: tìm theo tree.treeId
    List<Measurement> findByTreeTreeId(UUID treeId);

    // Lấy measurement theo ngày
    @Query("SELECT m FROM Measurement m WHERE DATE(m.measuredAt) = :date")
    List<Measurement> findByMeasuredAt(@Param("date") LocalDate date);

    // Lấy measurement theo khoảng ngày
    @Query("SELECT m FROM Measurement m WHERE DATE(m.measuredAt) BETWEEN :start AND :end")
    List<Measurement> findByMeasuredAtBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT m FROM Measurement m WHERE m.tree = :tree ORDER BY m.measuredAt DESC LIMIT 1")
    Optional<Measurement> findTopByTreeOrderByMeasuredAtDesc(Tree tree);
}


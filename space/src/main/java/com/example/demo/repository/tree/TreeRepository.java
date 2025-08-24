package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TreeRepository extends JpaRepository<Tree, UUID> {
    @Query("SELECT t FROM Tree t WHERE t.treeId = :treeId")
    Optional<Tree> findTreeById(@Param("treeId") UUID treeId);

    // Lấy tất cả cây theo zoneId
    @Query("SELECT t FROM Tree t WHERE t.zone.zoneId = :zoneId")
    List<Tree> findByZoneId(@Param("zoneId") UUID zoneId);

    // Lấy tất cả cây theo speciesId
    @Query("SELECT t FROM Tree t WHERE t.species.speciesId = :speciesId")
    List<Tree> findBySpeciesId(@Param("speciesId") UUID speciesId);

    // Tìm cây theo plantedDate
    @Query("SELECT t FROM Tree t WHERE t.plantedDate = :plantedDate")
    List<Tree> findByPlantedDate(@Param("plantedDate") LocalDate plantedDate);

    // Tìm cây theo code (exact match)
    @Query("SELECT t FROM Tree t WHERE t.code = :code")
    Tree findByCode(@Param("code") String code);

    // Tìm cây theo plantedDate range
    @Query("SELECT t FROM Tree t WHERE t.plantedDate BETWEEN :startDate AND :endDate")
    List<Tree> findByPlantedDateBetween(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Tìm cây theo code (pattern, LIKE)
    @Query("SELECT t FROM Tree t WHERE t.code LIKE %:code%")
    List<Tree> searchByCode(@Param("code") String code);

    @Query("SELECT COUNT(t) FROM Tree t WHERE t.zone.zoneId = :zoneId")
    long countByZoneId(@Param("zoneId") UUID zoneId);

    List<Tree> findByZoneZoneIdAndDeletedAtIsNull(UUID zoneId);
}

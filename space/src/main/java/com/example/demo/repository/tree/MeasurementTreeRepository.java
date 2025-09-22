package com.example.demo.repository.tree;

import com.example.SmartBuildingBackend.model.entity.tree.MeasurementTree;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeasurementTreeRepository extends JpaRepository<MeasurementTree, Long> {

    List<MeasurementTree> findByTreeTreeId(Long treeId);

    @Query("SELECT m FROM MeasurementTree m WHERE DATE(m.measuredAt) = :date")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<MeasurementTree> findByMeasuredAt(@Param("date") LocalDate date);

    @Query("SELECT m FROM MeasurementTree m WHERE DATE(m.measuredAt) BETWEEN :start AND :end")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<MeasurementTree> findByMeasuredAtBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
        SELECT m FROM MeasurementTree m 
        JOIN FETCH m.tree t 
        WHERE t.code = :code 
        ORDER BY m.measuredAt DESC
        """)
    Optional<MeasurementTree> findLatestTreeByCode(@Param("code") String code);


    @Query("SELECT COALESCE(SUM(m.leafAreaM2), 0) " +
            "FROM MeasurementTree m " +
            "WHERE m.tree.zone.zoneId = :zoneId " +
            "AND m.tree.deletedAt IS NULL " +
            "AND m.measurementId IN (" +
            "    SELECT MAX(m2.measurementId) " +
            "    FROM MeasurementTree m2 " +
            "    WHERE m2.tree = m.tree " +
            "    GROUP BY m2.tree" +
            ")")
    Optional<BigDecimal> findTotalLeafAreaByZoneId(@Param("zoneId") Long zoneId);

    /**
     * Get total leaf area for entire system (sum of latest measurements for all trees)
     */
    @Query("SELECT COALESCE(SUM(m.leafAreaM2), 0) " +
            "FROM MeasurementTree m " +
            "WHERE m.tree.deletedAt IS NULL " +
            "AND m.measurementId IN (" +
            "    SELECT MAX(m2.measurementId) " +
            "    FROM MeasurementTree m2 " +
            "    WHERE m2.tree = m.tree " +
            "    GROUP BY m2.tree" +
            ")")
    Optional<BigDecimal> findTotalLeafAreaSystem();


    @Query("""
        SELECT s.localName,
               COALESCE(SUM(m.leafAreaM2), 0),
               COALESCE(SUM(m.co2AbsorbedKg), 0),
               COALESCE(SUM(m.o2ReleasedKg), 0)
        FROM MeasurementTree m
        JOIN m.tree t
        LEFT JOIN t.species s
        GROUP BY s.localName
    """)
    List<Object[]> aggregateBySpecies();

    @Query("""
        SELECT z.zoneName,
               COALESCE(SUM(m.biomassKg), 0),
               COALESCE(SUM(m.carbonKg), 0),
               COALESCE(SUM(m.co2AbsorbedKg), 0),
               COALESCE(SUM(m.o2ReleasedKg), 0),
               COALESCE(SUM(m.leafAreaM2), 0)
        FROM MeasurementTree m
        JOIN m.tree t
        LEFT JOIN t.zone z
        WHERE YEAR(m.createdAt) = :year
        GROUP BY z.zoneName
    """)
    List<Object[]> summarizeByYearAndZone(@Param("year") int year);



    @Query(value = """
    SELECT
        COALESCE(SUM(m.biomass_kg), 0) AS biomass,
        COALESCE(SUM(m.carbon_kg), 0) AS carbon,
        COALESCE(SUM(m.co2_absorbed_kg), 0) AS co2,
        COALESCE(SUM(m.o2_released_kg), 0) AS o2,
        COALESCE(SUM(m.leaf_area_m2), 0) AS leafArea,
        COALESCE(SUM(m.water_loss), 0) AS waterLoss
    FROM measurement_tree m
    INNER JOIN (
        SELECT mt.tree_id, MAX(mt.measured_at) AS latest_measured_at
        FROM measurement_tree mt
        GROUP BY mt.tree_id
    ) latest ON m.tree_id = latest.tree_id AND m.measured_at = latest.latest_measured_at
    """, nativeQuery = true)
    List<Object[]> summarizeLatestMeasurements();

    @Query("""
    SELECT m FROM MeasurementTree m
    JOIN FETCH m.tree t
    WHERE t.treeId IN :treeIds
      AND m.measurementId = (
        SELECT MAX(m2.measurementId)
        FROM MeasurementTree m2
        WHERE m2.tree.treeId = t.treeId
      )
""")
    List<MeasurementTree> findLatestMeasurementsByTreeIds(@Param("treeIds") List<Long> treeIds);


}

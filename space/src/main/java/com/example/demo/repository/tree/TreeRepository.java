package com.example.demo.repository.tree;

import com.example.SmartBuildingBackend.model.entity.tree.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface TreeRepository extends JpaRepository<Tree, Long> {

    Optional<Tree> findByTreeId(Long treeId);

    @Query("SELECT t FROM Tree t " +
            "LEFT JOIN FETCH t.species s " +
            "LEFT JOIN FETCH t.zone z " +
            "WHERE t.deletedAt IS NULL AND z.isActive = true " +
            "ORDER BY t.treeId")
    List<Tree> findAllActiveTreesWithSpeciesAndZone();

    List<Tree> findByZoneZoneId(Long zoneId);

    List<Tree> findBySpeciesSpeciesId(Long speciesId);

    List<Tree> findByPlantedDate(LocalDate plantedDate);

    Optional<Tree> findByCode(String code);

    List<Tree> findByPlantedDateBetween(LocalDate startDate, LocalDate endDate);

    List<Tree> findByCodeContaining(String code);

    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END 
        FROM Tree t 
        WHERE t.latitude BETWEEN :lat - 0.0001 AND :lat + 0.0001 
        AND t.longitude BETWEEN :lng - 0.0001 AND :lng + 0.0001
        """)
    boolean existsByCoordinates(@Param("lat") Double latitude, @Param("lng") Double longitude);

    @Query(value = """
        SELECT COALESCE(
            MAX(CAST(
                CASE 
                    WHEN CHARINDEX('_T', t.code) > 0 
                    THEN SUBSTRING(t.code, CHARINDEX('_T', t.code) + 2, LEN(t.code)) 
                    ELSE '0' 
                END AS BIGINT
            )), 0
        ) 
        FROM tree t 
        WHERE t.zone_id = :zoneId 
        AND t.code LIKE '%_T%'
        """, nativeQuery = true)
    Long findMaxIndexByZone(@Param("zoneId") Long zoneId);

    @Query(value = """
        SELECT t.zone_id AS zoneId, 
               COALESCE(
                   MAX(CAST(
                       CASE 
                           WHEN CHARINDEX('_T', t.code) > 0 
                           THEN SUBSTRING(t.code, CHARINDEX('_T', t.code) + 2, LEN(t.code)) 
                           ELSE '0' 
                       END AS BIGINT
                   )), 0
               ) AS maxIndex
        FROM tree t 
        WHERE t.code LIKE '%_T%'
        GROUP BY t.zone_id
        """, nativeQuery = true)
    List<Object[]> findMaxIndexByAllZonesRaw();

    default Map<Long, Long> findMaxIndexByAllZones() {
        return findMaxIndexByAllZonesRaw().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // zoneId
                        row -> (Long) row[1]  // maxIndex
                ));
    }

    @Query("""
        SELECT t FROM Tree t 
        LEFT JOIN FETCH t.species s 
        WHERE t.zone.zoneId = :zoneId 
        AND t.deletedAt IS NULL
        """)
    List<Tree> findByZoneZoneIdAndDeletedAtIsNullWithSpecies(@Param("zoneId") Long zoneId);

    @Query("SELECT t FROM Tree t LEFT JOIN FETCH t.species WHERE t.treeId = :treeId")
    Optional<Tree> findByTreeIdWithSpecies(@Param("treeId") Long treeId);

    @Query("SELECT COUNT(t) FROM Tree t WHERE t.zone.zoneId = :zoneId AND t.deletedAt IS NULL")
    Long countActiveTreesByZoneId(@Param("zoneId") Long zoneId);

    @Query("SELECT COUNT(t) FROM Tree t WHERE t.deletedAt IS NULL")
    Long countActiveTrees();

    @Query("SELECT t.code FROM Tree t")
    List<String> findAllCodes();

    @Query("SELECT t.latitude, t.longitude FROM Tree t")
    List<Object[]> findAllCoordinates();

    @Query("SELECT t.latitude, t.longitude, t.code FROM Tree t WHERE t.deletedAt IS NULL")
    List<Object[]> findAllCoordinatesAndCodes();

    @Query("""
    SELECT t 
    FROM Tree t
    JOIN FETCH t.species s
    WHERE s.localName = :localName
""")
    List<Tree> findBySpeciesLocalName(@Param("localName") String localName);


}
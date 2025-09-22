package com.example.demo.repository.tree;

import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByZoneName(String zoneName);

    Optional<Zone> findByZoneId(Long zoneId);

    boolean existsByZoneName(String zoneName);

    boolean existsByZoneNameAndZoneIdNot(String zoneName, Long zoneId);

    List<Zone> findAllByIsActiveTrue();

}
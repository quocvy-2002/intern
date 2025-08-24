package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {

    Optional<Zone> findByZoneName(String zoneName);

    Optional<Zone> findByZoneId(UUID zoneId);

    boolean existsByZoneName(String zoneName);

    boolean existsByZoneNameAndZoneIdNot(String zoneName, UUID zoneId);

    List<Zone> findAllByIsActiveTrue();
}
package com.example.demo.repository.tree;

import com.example.SmartBuildingBackend.model.entity.tree.CarbonSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonSnapshotRepository extends JpaRepository<CarbonSnapshot, Long> {
}

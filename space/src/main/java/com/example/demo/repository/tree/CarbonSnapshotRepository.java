package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.CarbonSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarbonSnapshotRepository extends JpaRepository<CarbonSnapshot, UUID> {
}

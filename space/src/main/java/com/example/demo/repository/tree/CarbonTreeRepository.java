package com.example.demo.repository.tree;

import com.example.demo.model.entity.tree.CarbonTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarbonTreeRepository extends JpaRepository<CarbonTree, UUID> {
}

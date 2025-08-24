package com.example.demo.service.tree;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.tree.CarbonTreeMapper;
import com.example.demo.model.dto.tree.CarbonTreeDTO;
import com.example.demo.model.entity.tree.CarbonTree;
import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.entity.tree.Species;
import com.example.demo.model.entity.tree.Tree;
import com.example.demo.repository.tree.CarbonTreeRepository;
import com.example.demo.repository.tree.MeasurementRepository;
import com.example.demo.repository.tree.TreeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarbonTreeService {

    TreeRepository treeRepository;
    MeasurementRepository measurementRepository;
    CarbonTreeRepository carbonTreeRepository;
    CarbonTreeMapper carbonTreeMapper;

    @Transactional
    public CarbonTreeDTO calculateCarbonForTree(UUID treeId) {
        Tree tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new AppException(ErrorCode.TREE_NOT_EXISTS));
        Measurement latestMeasurement = measurementRepository.findTopByTreeOrderByMeasuredAtDesc(tree)
                .orElseThrow(() -> new AppException(ErrorCode.MEASUREMENT_CREATION_FAILED));
        Species species = tree.getSpecies();
        if (species == null || species.getCoeffB0() == null || species.getCoeffB1() == null || species.getCoeffB2() == null) {
            throw new IllegalStateException("Species or coefficients not found for tree: " + treeId);
        }
        BigDecimal dbhSquared = latestMeasurement.getDbhCm().pow(2);
        BigDecimal biomass = species.getCoeffB0()
                .add(species.getCoeffB1().multiply(dbhSquared))
                .add(species.getCoeffB2().multiply(latestMeasurement.getHeightM()));
        BigDecimal carbon = biomass.multiply(BigDecimal.valueOf(0.5));
        CarbonTree carbonTree = CarbonTree.builder()
                .recordId(UUID.randomUUID())
                .tree(tree)
                .carbonKg(carbon.setScale(2, RoundingMode.HALF_UP))
                .calculatedAt(LocalDateTime.now())
                .build();
        carbonTree = carbonTreeRepository.save(carbonTree);
        CarbonTreeDTO dto = carbonTreeMapper.toDto(carbonTree);
        dto.setCo2Kg(convertToCO2(carbon).setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    public BigDecimal convertToCO2(BigDecimal carbonKg) {
        return carbonKg.multiply(BigDecimal.valueOf(3.67));
    }
}
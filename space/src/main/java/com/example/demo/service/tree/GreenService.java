package com.example.demo.service.tree;

import com.example.SmartBuildingBackend.model.dto.tree.GreenDTO;
import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import com.example.SmartBuildingBackend.repository.tree.MeasurementTreeRepository;
import com.example.SmartBuildingBackend.repository.tree.TreeRepository;
import com.example.SmartBuildingBackend.repository.tree.ZoneRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class GreenService {

    ZoneRepository zoneRepository;
    TreeRepository treeRepository;
    MeasurementTreeRepository measurementTreeRepository;

    static final BigDecimal HECTARE_TO_M2 = new BigDecimal("10000");

    /**
     * Convert hectare to square meter
     */
    private BigDecimal toM2(BigDecimal hectare) {
        return hectare == null ? BigDecimal.ZERO : hectare.multiply(HECTARE_TO_M2);
    }

    /**
     * Calculate GNPR for multiple zones in batch
     */
    public Map<Long, GreenDTO> calculateGnprBatch(List<Long> zoneIds) {
        Map<Long, GreenDTO> result = new HashMap<>();
        for (Long zoneId : zoneIds) {
            result.put(zoneId, calculateGnpr(zoneId));
        }
        return result;
    }

    /**
     * Calculate Green Net Productivity Ratio (GNPR) for a specific zone
     * GNPR Gross = Total Leaf Area / Zone Area
     * GNPR Net = Total Leaf Area / Effective Green Area
     */
    public GreenDTO calculateGnpr(Long zoneId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + zoneId));

        BigDecimal totalLeafArea = getTotalLeafAreaByZoneId(zoneId);
        int treeCount = getTreeCountByZoneId(zoneId);

        BigDecimal zoneAreaM2 = zone.getArea() != null ? zone.getArea() : BigDecimal.ZERO;
        BigDecimal zoneNonGreenAreaM2 = zone.getNonGreenArea() != null ? zone.getNonGreenArea() : BigDecimal.ZERO;

        BigDecimal gnprGross = BigDecimal.ZERO;
        if (zoneAreaM2.compareTo(BigDecimal.ZERO) > 0) {
            gnprGross = totalLeafArea.divide(zoneAreaM2, 4, RoundingMode.HALF_UP);
        }

        BigDecimal greenCoverPercent = BigDecimal.ZERO;
        BigDecimal greenArea = zoneAreaM2.subtract(zoneNonGreenAreaM2);
        if (zoneAreaM2.compareTo(BigDecimal.ZERO) > 0 && greenArea.compareTo(BigDecimal.ZERO) > 0) {
            greenCoverPercent = greenArea.divide(zoneAreaM2, 4, RoundingMode.HALF_UP);
        }

        BigDecimal gnprNet = BigDecimal.ZERO;
        if (greenCoverPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal effectiveGreenArea = zoneAreaM2.multiply(greenCoverPercent);
            if (effectiveGreenArea.compareTo(BigDecimal.ZERO) > 0) {
                gnprNet = totalLeafArea.divide(effectiveGreenArea, 4, RoundingMode.HALF_UP);
            }
        }

        return GreenDTO.builder()
                .area(zoneAreaM2)
                .treeCount(treeCount)
                .gnprGross(gnprGross)
                .gnprNet(gnprNet)
                .build();
    }

    public GreenDTO calculateSystemGnpr() {
        List<Zone> zones = zoneRepository.findAll();

        BigDecimal totalLeafArea = getTotalLeafAreaSystem();
        int totalTreeCount = getTotalTreeCountSystem();

        BigDecimal totalArea = BigDecimal.ZERO;
        BigDecimal totalNonGreenArea = BigDecimal.ZERO;

        for (Zone zone : zones) {
            BigDecimal zoneAreaM2 = zone.getArea() != null ? zone.getArea() : BigDecimal.ZERO;
            BigDecimal zoneNonGreenAreaM2 = zone.getNonGreenArea() != null ? zone.getNonGreenArea() : BigDecimal.ZERO;

            totalArea = totalArea.add(zoneAreaM2);
            totalNonGreenArea = totalNonGreenArea.add(zoneNonGreenAreaM2);
        }

        BigDecimal gnprGross = BigDecimal.ZERO;
        if (totalArea.compareTo(BigDecimal.ZERO) > 0) {
            gnprGross = totalLeafArea.divide(totalArea, 4, RoundingMode.HALF_UP);
        }

        BigDecimal greenCoverPercent = BigDecimal.ZERO;
        BigDecimal totalGreenArea = totalArea.subtract(totalNonGreenArea);
        if (totalArea.compareTo(BigDecimal.ZERO) > 0 && totalGreenArea.compareTo(BigDecimal.ZERO) > 0) {
            greenCoverPercent = totalGreenArea.divide(totalArea, 4, RoundingMode.HALF_UP);
        }

        BigDecimal gnprNet = BigDecimal.ZERO;
        if (greenCoverPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal effectiveGreenArea = totalArea.multiply(greenCoverPercent);
            if (effectiveGreenArea.compareTo(BigDecimal.ZERO) > 0) {
                gnprNet = totalLeafArea.divide(effectiveGreenArea, 4, RoundingMode.HALF_UP);
            }
        }

        return GreenDTO.builder()
                .area(totalArea)
                .treeCount(totalTreeCount)
                .gnprGross(gnprGross)
                .gnprNet(gnprNet)
                .build();
    }

    private BigDecimal getTotalLeafAreaByZoneId(Long zoneId) {
        return measurementTreeRepository
                .findTotalLeafAreaByZoneId(zoneId)
                .orElse(BigDecimal.ZERO);
    }

    private int getTreeCountByZoneId(Long zoneId) {
        return treeRepository.countActiveTreesByZoneId(zoneId).intValue();
    }

    private BigDecimal getTotalLeafAreaSystem() {
        return measurementTreeRepository
                .findTotalLeafAreaSystem()
                .orElse(BigDecimal.ZERO);
    }

    private int getTotalTreeCountSystem() {
        return treeRepository.countActiveTrees().intValue();
    }
}
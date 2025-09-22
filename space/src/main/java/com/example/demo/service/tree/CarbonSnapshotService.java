package com.example.demo.service.tree;

import com.example.SmartBuildingBackend.model.dto.tree.CarbonSummary;
import com.example.SmartBuildingBackend.model.dto.tree.CarbonSummaryByZone;
import com.example.SmartBuildingBackend.model.dto.tree.SpeciesContribution;
import com.example.SmartBuildingBackend.repository.tree.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarbonSnapshotService {
    MeasurementTreeRepository measurementTreeRepository;

    public CarbonSummary summarize() {
        List<Object[]> rows = measurementTreeRepository.summarizeLatestMeasurements();

        if (rows.isEmpty()) {
            return new CarbonSummary(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        Object[] result = rows.get(0);

        BigDecimal biomass   = toBigDecimal(result[0]);
        BigDecimal carbon    = toBigDecimal(result[1]);
        BigDecimal co2       = toBigDecimal(result[2]);
        BigDecimal o2        = toBigDecimal(result[3]);
        BigDecimal leafArea  = toBigDecimal(result[4]);
        BigDecimal waterLoss = toBigDecimal(result[5]);

        return new CarbonSummary(biomass, carbon, co2, o2, leafArea, waterLoss);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        if (value instanceof String str) {
            try {
                return new BigDecimal(str);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }




    public List<CarbonSummaryByZone> summarizeByYearAndZone(int year) {
        return measurementTreeRepository.summarizeByYearAndZone(year).stream()
                .map(row -> new CarbonSummaryByZone(
                        (String) row[0],
                        (BigDecimal) row[1],
                        (BigDecimal) row[2],
                        (BigDecimal) row[3],
                        (BigDecimal) row[4],
                        (BigDecimal) row[5],
                        BigDecimal.ZERO
                ))
                .toList();
    }

    public List<SpeciesContribution> topSpeciesByO2(int limit) {
        return measurementTreeRepository.aggregateBySpecies().stream()
                .map(row -> new SpeciesContribution(
                        row[0] != null ? (String) row[0] : "Unknown",
                        (BigDecimal) row[1],
                        (BigDecimal) row[2],
                        (BigDecimal) row[3]
                ))
                .sorted(Comparator.comparing(SpeciesContribution::getO2).reversed())
                .limit(limit)
                .toList();
    }

    public List<SpeciesContribution> topSpeciesByLeafArea(int limit) {
        return topSpeciesByO2(Integer.MAX_VALUE).stream()
                .sorted(Comparator.comparing(SpeciesContribution::getLeafArea).reversed())
                .limit(limit)
                .toList();
    }

    public List<SpeciesContribution> topSpeciesByCO2(int limit) {
        return topSpeciesByO2(Integer.MAX_VALUE).stream()
                .sorted(Comparator.comparing(SpeciesContribution::getCo2).reversed())
                .limit(limit)
                .toList();
    }
}

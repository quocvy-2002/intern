package com.example.demo.service.tree;


import com.example.SmartBuildingBackend.exceptionManagement.ExceptionFactory;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.MeasurementTreeError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.SpeciesError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.TreeError;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.ValidationError;
import com.example.SmartBuildingBackend.mapper.tree.MeasurementTreeMapper;
import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementDTO;
import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementUpdateDTO;
import com.example.SmartBuildingBackend.model.entity.tree.MeasurementTree;
import com.example.SmartBuildingBackend.model.entity.tree.Species;
import com.example.SmartBuildingBackend.model.entity.tree.Tree;
import com.example.SmartBuildingBackend.repository.tree.MeasurementTreeRepository;
import com.example.SmartBuildingBackend.repository.tree.SpeciesRepository;
import com.example.SmartBuildingBackend.repository.tree.TreeRepository;
import com.example.SmartBuildingBackend.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeasurementService {
    private static final Logger log = LoggerFactory.getLogger(MeasurementService.class);

    MeasurementTreeRepository measurementRepository;
    MeasurementTreeMapper measurementMapper;
    TreeRepository treeRepository;
    ExcelHelper excelHelper;
    SpeciesRepository speciesRepository;
    ExceptionFactory exceptionFactory;

    private static final BigDecimal PI = new BigDecimal("3.14159265358979323846");
    private static final BigDecimal BIOMASS_COEFF = new BigDecimal("0.0509");
    private static final BigDecimal CARBON_RATIO = BigDecimal.valueOf(0.47);
    private static final BigDecimal CO2_RATIO = BigDecimal.valueOf(3.67);
    private static final BigDecimal O2_CO2_MOLAR_RATIO = BigDecimal.valueOf(32.0 / 44.0);

    // ==================== PUBLIC API METHODS ====================

    public List<MeasurementDTO> getMeasurementsByTreeId(Long treeId) {
        validateTreeExists(treeId);
        List<MeasurementTree> measurements = measurementRepository.findByTreeTreeId(treeId);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public List<MeasurementTree> getMeasurementsByTree(Long treeId) {
        validateTreeExists(treeId);
        return measurementRepository.findByTreeTreeId(treeId);
    }

    public List<MeasurementDTO> getAllMeasurements() {
        List<MeasurementTree> measurements = measurementRepository.findAll();
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public List<MeasurementDTO> getMeasurementsByDate(LocalDate date) {
        List<MeasurementTree> measurements = measurementRepository.findByMeasuredAt(date);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public List<MeasurementDTO> getMeasurementsByDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw exceptionFactory.createValidationException(
                    "DateRange",
                    "start/end",
                    start + " - " + end,
                    ValidationError.INVALID_DATE_RANGE
            );
        }

        List<MeasurementTree> measurements = measurementRepository.findByMeasuredAtBetween(start, end);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public Workbook exportAllMeasurementsToExcel() {
        List<MeasurementTree> measurements = measurementRepository.findAll();
        return excelHelper.exportMeasurementsToExcel(measurements);
    }

    public Workbook exportMeasurementsByTreeToExcel(Long treeId) {
        validateTreeExists(treeId);
        List<MeasurementTree> measurements = measurementRepository.findByTreeTreeId(treeId);
        return excelHelper.exportMeasurementsToExcel(measurements);
    }

    @Transactional
    public MeasurementDTO createMeasurement(Long treeId, MeasurementUpdateDTO request) {
        Tree tree = validateTreeExists(treeId);
        Species species = tree.getSpecies();
        if (species == null || species.getLocalName() == null) {
            throw  exceptionFactory.createNotFoundException("Species", "species",species, SpeciesError.SPECIES_NOT_FOUND);
        }

        MeasurementTree measurement = new MeasurementTree();
        measurement.setTree(tree);
        measurementMapper.updateMeasurement(measurement, request);
        updateMeasurementCalculations(measurement, species);
        measurement.setCreatedAt(LocalDateTime.now());
        measurement.setMeasuredAt(request.getMeasuredAt() != null ? request.getMeasuredAt() : LocalDateTime.now());

        MeasurementTree savedMeasurement = measurementRepository.save(measurement);
        return measurementMapper.toMeasurementDTO(savedMeasurement);
    }

    @Transactional
    public MeasurementDTO updateMeasurement(Long measurementId, MeasurementUpdateDTO request) {
        MeasurementTree measurement = measurementRepository.findById(measurementId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("MeasurementTree","measurementId",measurementId, MeasurementTreeError.MEASUREMENT_TREE_NOT_FOUND));

        Tree tree = measurement.getTree();
        Species species = speciesRepository.findByLocalName(tree.getSpecies().getLocalName())
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Species","LocalName",tree.getSpecies().getLocalName(), SpeciesError.SPECIES_NOT_FOUND));

        measurementMapper.updateMeasurement(measurement, request);
        updateMeasurementCalculations(measurement, species);
        measurement.setCreatedAt(LocalDateTime.now());

        MeasurementTree updated = measurementRepository.save(measurement);
        return measurementMapper.toMeasurementDTO(updated);
    }

    // ==================== HELPER METHODS ====================

    private Tree validateTreeExists(Long treeId) {
        return treeRepository.findByTreeId(treeId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Tree","treeId",treeId, TreeError.TREE_NOT_FOUND));
    }

    public void updateMeasurementCalculations(MeasurementTree measurement, Species species) {
        BigDecimal leafArea = null;
        if (measurement.getCanopyDiameterM() != null && species.getLai() != null) {
            BigDecimal radius = measurement.getCanopyDiameterM().divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
            BigDecimal canopyArea = PI.multiply(radius.pow(2)); // π * (d/2)^2
            leafArea = canopyArea
                    .multiply(BigDecimal.valueOf(species.getLai()))
                    .setScale(4, RoundingMode.HALF_UP);

            measurement.setLeafAreaM2(leafArea);
        } else {
            measurement.setLeafAreaM2(BigDecimal.ZERO);
        }

        // Biomass, Carbon, CO2, O2
        if (measurement.getGirthCm() != null && measurement.getHeightM() != null &&
                species.getWoodDensity() != null) {

            // DBH (cm) = girth(cm) / π
            BigDecimal dbhCm = measurement.getGirthCm().divide(PI, RoundingMode.HALF_UP);

            // term = ρ * DBH^2(cm²) * H(m)
            BigDecimal dbhSquared = dbhCm.pow(2);
            BigDecimal term = species.getWoodDensity()
                    .multiply(dbhSquared)
                    .multiply(measurement.getHeightM());

            // Biomass (kg) = 0.0509 * (ρ * DBH² * H)
            BigDecimal biomass = BIOMASS_COEFF
                    .multiply(term)
                    .setScale(4, RoundingMode.HALF_UP);

            BigDecimal carbon = biomass.multiply(CARBON_RATIO).setScale(4, RoundingMode.HALF_UP);
            BigDecimal co2 = carbon.multiply(CO2_RATIO).setScale(4, RoundingMode.HALF_UP);
            BigDecimal o2 = co2.multiply(O2_CO2_MOLAR_RATIO).setScale(4, RoundingMode.HALF_UP);

            measurement.setBiomassKg(biomass);
            measurement.setCarbonKg(carbon);
            measurement.setCo2AbsorbedKg(co2);
            measurement.setO2ReleasedKg(o2);
        } else {
            measurement.setBiomassKg(BigDecimal.ZERO);
            measurement.setCarbonKg(BigDecimal.ZERO);
            measurement.setCo2AbsorbedKg(BigDecimal.ZERO);
            measurement.setO2ReleasedKg(BigDecimal.ZERO);
        }

        BigDecimal plantArea = leafArea.multiply(leafArea).multiply(BigDecimal.valueOf(0.7854));
        BigDecimal gallonsTreePerDay = plantArea.multiply(BigDecimal.valueOf(0.623)).multiply(BigDecimal.valueOf(0.2).multiply(species.getPlantFactor()));
        measurement.setWaterLoss(gallonsTreePerDay);
    }

}
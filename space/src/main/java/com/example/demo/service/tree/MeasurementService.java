package com.example.demo.service.tree;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.tree.MeasurementMapper;
import com.example.demo.model.dto.tree.measurement.MeasurementCreateDTO;
import com.example.demo.model.dto.tree.measurement.MeasurementDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.model.dto.tree.tree.TreeMeasurementDTO;
import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.entity.tree.Tree;
import com.example.demo.repository.tree.MeasurementRepository;
import com.example.demo.repository.tree.TreeRepository;
import com.example.demo.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeasurementService {
    MeasurementRepository  measurementRepository;
    MeasurementMapper measurementMapper;
    TreeRepository  treeRepository;
    ExcelHelper excelHelper;
    TreeService treeService;

    public MeasurementDTO createMeasurement(MeasurementCreateDTO request) {
        Tree tree = treeRepository.findTreeById(request.getTreeId())
                .orElseThrow(() -> new AppException(ErrorCode.TREE_NOT_EXISTS));
        Measurement measurement = measurementMapper.toMeasurement(request);
        measurement.setTree(tree);
        LocalDateTime now = LocalDateTime.now();
        measurement.setCreatedAt(now);
        return measurementMapper.toMeasurementDTO(measurementRepository.save(measurement));
    }

    public List<MeasurementDTO> createMeasurementList(List<MeasurementCreateDTO> requests) {
        List<MeasurementDTO> result = new ArrayList<>();
        List<Measurement> measurementsToSave = new ArrayList<>();

        for (MeasurementCreateDTO req : requests) {
            Tree tree = treeRepository.findTreeById(req.getTreeId())
                    .orElseThrow(() -> new AppException(ErrorCode.TREE_NOT_EXISTS));
            Measurement measurement = measurementMapper.toMeasurement(req);
            measurement.setTree(tree);
            measurement.setCreatedAt(LocalDateTime.now());
            measurementsToSave.add(measurement);
        }

        measurementRepository.saveAll(measurementsToSave);
        for (Measurement m : measurementsToSave) {
            result.add(measurementMapper.toMeasurementDTO(m));
        }
        return result;
    }

    public List<MeasurementDTO> createMeasurementsFromExcel(MultipartFile file) {
        List<TreeMeasurementDTO> treeMeasurementList = excelHelper.excelToTreeMeasurements(file);
        List<MeasurementDTO> savedMeasurements = new ArrayList<>();
        List<Measurement> measurementsToSave = new ArrayList<>();

        for (TreeMeasurementDTO tm : treeMeasurementList) {
            // 1. Lưu tree từ TreeCreateDTO
            TreeDTO savedTree = treeService.createTree(tm.getTree()); // tm.getTree() là TreeCreateDTO

            // 2. Lấy entity Tree từ DB
            Tree treeEntity = treeRepository.findTreeById(savedTree.getTreeId())
                    .orElseThrow(() -> new AppException(ErrorCode.TREE_NOT_EXISTS));

            // 3. Tạo measurement
            Measurement measurement = Measurement.builder()
                    .measurementId(UUID.randomUUID())
                    .tree(treeEntity)
                    .dbhCm(tm.getDbhCm())
                    .heightM(tm.getHeightM())
                    .canopyDiameterM(tm.getCanopyDiameterM())
                    .healthStatus(tm.getHealthStatus())
                    .measuredAt(tm.getMeasuredAt())
                    .createdAt(LocalDateTime.now())
                    .build();

            measurementsToSave.add(measurement);
        }

        measurementRepository.saveAll(measurementsToSave);

        for (Measurement m : measurementsToSave) {
            savedMeasurements.add(measurementMapper.toMeasurementDTO(m));
        }

        return savedMeasurements;
    }


    public List<MeasurementDTO> getMeasurementsByTreeId(UUID treeId) {
        List<Measurement> measurements = measurementRepository.findByTreeTreeId(treeId);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public List<MeasurementDTO> getMeasurementsByDate(LocalDate date) {
        List<Measurement> measurements = measurementRepository.findByMeasuredAt(date);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public List<MeasurementDTO> getMeasurementsByDateRange(LocalDate start, LocalDate end) {
        List<Measurement> measurements = measurementRepository.findByMeasuredAtBetween(start, end);
        return measurements.stream()
                .map(measurementMapper::toMeasurementDTO)
                .toList();
    }

    public Workbook exportAllMeasurementsToExcel() {
        List<Measurement> measurements = measurementRepository.findAll();
        return excelHelper.exportMeasurementsToExcel(measurements);
    }

    public Workbook exportMeasurementsByTreeToExcel(UUID treeId) {
        List<Measurement> measurements = measurementRepository.findByTreeTreeId(treeId);
        return excelHelper.exportMeasurementsToExcel(measurements);
    }
}

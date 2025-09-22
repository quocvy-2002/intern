package com.example.demo.service.tree;

import com.example.SmartBuildingBackend.exceptionManagement.ExceptionFactory;
import com.example.SmartBuildingBackend.exceptionManagement.errorCategories.*;
import com.example.SmartBuildingBackend.mapper.tree.MeasurementTreeMapper;
import com.example.SmartBuildingBackend.mapper.tree.TreeMapper;
import com.example.SmartBuildingBackend.model.dto.tree.tree.*;
import com.example.SmartBuildingBackend.model.entity.tree.MeasurementTree;
import com.example.SmartBuildingBackend.model.entity.tree.Species;
import com.example.SmartBuildingBackend.model.entity.tree.Tree;
import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import com.example.SmartBuildingBackend.repository.tree.MeasurementTreeRepository;
import com.example.SmartBuildingBackend.repository.tree.SpeciesRepository;
import com.example.SmartBuildingBackend.repository.tree.TreeRepository;
import com.example.SmartBuildingBackend.repository.tree.ZoneRepository;
import com.example.SmartBuildingBackend.service.excel.ExcelHelper;
import com.example.SmartBuildingBackend.utils.TreeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int BATCH_SIZE = 1000;
    private static final int SAVE_BATCH_SIZE = 200;

    TreeRepository treeRepository;
    TreeMapper treeMapper;
    SpeciesRepository speciesRepository;
    ZoneRepository zoneRepository;
    ExcelHelper excelHelper;
    MeasurementTreeRepository measurementRepository;
    MeasurementTreeMapper measurementMapper;
    TreeUtils treeUtils;
    ExceptionFactory exceptionFactory;

    @Transactional
    public TreeDTO createTree(TreeCreateDTO request) {
        return createTreeList(List.of(request)).get(0);
    }

    public List<TreeDTO> getAllTrees() {
        return treeRepository.findAll()
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    public List<TreeMapDTO> getAllTreeMap() {
        List<Tree> trees = treeRepository.findAll();
        List<Long> treeIds = trees.stream()
                .map(Tree::getTreeId)
                .toList();

        List<MeasurementTree> latestMeasurements = measurementRepository.findLatestMeasurementsByTreeIds(treeIds);
        Map<Long, MeasurementTree> measurementMap = latestMeasurements.stream()
                .collect(Collectors.toMap(m -> m.getTree().getTreeId(), m -> m));
        return trees.stream()
                .map(tree -> {
                    MeasurementTree latestMeasurement = measurementMap.get(tree.getTreeId());
                    return treeMapper.toTreeMapDTO(tree, latestMeasurement);
                })
                .collect(Collectors.toList());
    }

    public List<TreeMapDTO> getAllTreeMapBySpecies(String localName) {
        List<Tree> trees = treeRepository.findBySpeciesLocalName(localName);

        List<Long> treeIds = trees.stream()
                .map(Tree::getTreeId)
                .toList();

        List<MeasurementTree> latestMeasurements = measurementRepository.findLatestMeasurementsByTreeIds(treeIds);

        Map<Long, MeasurementTree> measurementMap = latestMeasurements.stream()
                .collect(Collectors.toMap(m -> m.getTree().getTreeId(), m -> m));

        return trees.stream()
                .map(tree -> {
                    MeasurementTree latestMeasurement = measurementMap.get(tree.getTreeId());
                    return treeMapper.toTreeMapDTO(tree, latestMeasurement);
                })
                .collect(Collectors.toList());
    }




    public MeasurementTree getLastMeasurement(Long treeId) {
        Tree tree = treeRepository.findByTreeId(treeId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", treeId, TreeError.TREE_NOT_FOUND
                ));
        return measurementRepository.findLatestTreeByCode(tree.getCode())
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "code", tree.getCode(), MeasurementTreeError.MEASUREMENT_TREE_NOT_FOUND
                ));
    }

    @Transactional
    public List<TreeDTO> createTreeList(List<TreeCreateDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            throw exceptionFactory.createValidationException(
                    "Request", "requests", requests, ValidationError.EMPTY_REQUEST_LIST
            );
        }

        BatchFetchResult batchData = treeUtils.fetchBatchData();
        Map<Integer, Map<String, Object>> validationResults = treeUtils.validateRequestsBatch(requests, batchData);

        List<Tree> validTrees = new ArrayList<>();
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            Map<String, Object> result = validationResults.get(i);
            if ((Boolean) result.get(TreeUtils.ValidationKey.VALID.name())) {
                validTrees.add(treeUtils.buildTreeFromValidation(result, batchData));
                validIndices.add(i);
            } else {
                TreeCreateDTO failedRequest = requests.get(i);
                System.err.println("Failed to create tree at index " + i +
                        " - Species: " + failedRequest.getLocalName() +
                        ", Zone: " + failedRequest.getZoneName());
            }
        }

        if (validTrees.isEmpty()) {
            throw exceptionFactory.createValidationException(
                    "Tree", "validTrees", validTrees, ValidationError.BATCH_VALIDATION_FAILED
            );
        }

        List<Tree> savedTrees = saveTreesBatch(validTrees);
        saveMeasurementsBatch(savedTrees, validIndices, requests);
        treeUtils.updateBatchDataAfterSave(batchData, savedTrees, requests, validIndices);

        return savedTrees.stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TreeDTO> createTreesWithMeasurements(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw exceptionFactory.createValidationException("File", "file", file, ValidationError.EMPTY_FILE);
        }

        List<TreeCreateDTO> requests = excelHelper.excelToTreeMeasurements(file);
        if (requests.isEmpty()) {
            throw exceptionFactory.createValidationException("Request", "request", requests, ValidationError.EMPTY_DATA_FILE);
        }

        List<TreeDTO> results = new ArrayList<>();
        BatchFetchResult batchData = treeUtils.fetchBatchData();

        for (int i = 0; i < requests.size(); i += BATCH_SIZE) {
            List<TreeCreateDTO> batch = requests.subList(i, Math.min(i + BATCH_SIZE, requests.size()));
            Map<Integer, Map<String, Object>> validationResults = treeUtils.validateRequestsBatch(batch, batchData);
            List<Tree> validTrees = new ArrayList<>();
            List<Integer> validIndices = new ArrayList<>();

            for (int j = 0; j < batch.size(); j++) {
                Map<String, Object> result = validationResults.get(j);
                if ((Boolean) result.get(TreeUtils.ValidationKey.VALID.name())) {
                    validTrees.add(treeUtils.buildTreeFromValidation(result, batchData));
                    validIndices.add(j);
                }
            }

            if (!validTrees.isEmpty()) {
                List<Tree> savedTrees = saveTreesBatch(validTrees);
                saveMeasurementsBatch(savedTrees, validIndices, batch);
                results.addAll(savedTrees.stream().map(treeMapper::toTreeDTO).collect(Collectors.toList()));
                treeUtils.updateBatchDataAfterSave(batchData, savedTrees, batch, validIndices); // Update in-place
            }
        }

        return results.isEmpty() ? List.of() : results;
    }

    @Transactional
    public TreeDTO updateTreeByCode(String code, TreeUpdateDTO request) {
        Tree tree = treeRepository.findByCode(code)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", code, TreeError.TREE_NOT_FOUND
                ));

        updateTreeFields(tree, request);
        try {
            Tree updatedTree = treeRepository.save(tree);
            return treeMapper.toTreeDTO(updatedTree);
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.createAlreadyExistsException(
                    "Tree", "code", tree.getCode(), TreeError.TREE_ALREADY_EXISTS
            );
        }
    }

    public List<TreeDTO> getTreesByZoneId(Long zoneId) {
        return treeRepository.findByZoneZoneIdAndDeletedAtIsNullWithSpecies(zoneId)
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    public List<TreeDTO> getTreesBySpeciesId(Long speciesId) {
        return treeRepository.findBySpeciesSpeciesId(speciesId)
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    public List<TreeDTO> getTreesByPlantedDate(LocalDate plantedDate) {
        return treeRepository.findByPlantedDate(plantedDate)
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    public TreeDTO getTreeByCode(String code) {
        Tree tree = treeRepository.findByCode(code)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", code, TreeError.TREE_NOT_FOUND
                ));
        return treeMapper.toTreeDTO(tree);
    }

    public List<String> getTreesLocalDateTimes(String code) {
        Tree tree = treeRepository.findByCode(code)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", code, TreeError.TREE_NOT_FOUND
                ));
        return measurementRepository.findByTreeTreeId(tree.getTreeId())
                .stream()
                .map(m -> m.getMeasuredAt().format(DATE_FORMATTER))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<TreeDTO> searchTreesByCode(String codePattern) {
        return treeRepository.findByCodeContaining(codePattern)
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    public List<TreeDTO> getTreesByPlantedDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw exceptionFactory.createValidationException(
                    "DateRange",
                    "start,end",
                    Map.of("start", start, "end", end),
                    ValidationError.INVALID_DATE_RANGE
            );
        }

        return treeRepository.findByPlantedDateBetween(start, end)
                .stream()
                .map(treeMapper::toTreeDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTree(String code) {
        Tree tree = treeRepository.findByCode(code)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", code, TreeError.TREE_NOT_FOUND
                ));
        tree.setDeletedAt(LocalDateTime.now());
        treeRepository.save(tree);
    }

    public Tree getTreeWithSpecies(Long treeId) {
        return treeRepository.findByTreeIdWithSpecies(treeId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Tree", "treeId", treeId, TreeError.TREE_NOT_FOUND
                ));
    }

    public Workbook exportAllTreesWithMeasurementsToExcel() {
        List<Tree> trees = treeRepository.findAllActiveTreesWithSpeciesAndZone();
        List<MeasurementTree> measurements = measurementRepository.findAll();
        List<Species> species = speciesRepository.findAll();

        try {
            return excelHelper.exportTreesWithMeasurementsToExcel(trees, measurements, species);
        } catch (Exception e) {
            throw exceptionFactory.createCustomException(
                    "Export",
                    List.of("errorDetail"),
                    List.of(e.getMessage()),
                    ValidationError.EXPORT_FAILED
            );
        }
    }

    private List<Tree> saveTreesBatch(List<Tree> trees) {
        try {
            List<Tree> savedTrees = new ArrayList<>();
            for (int i = 0; i < trees.size(); i += SAVE_BATCH_SIZE) {
                List<Tree> batch = trees.subList(i, Math.min(i + SAVE_BATCH_SIZE, trees.size()));
                savedTrees.addAll(treeRepository.saveAll(batch));
            }
            treeRepository.flush(); // Flush cuối cùng
            return savedTrees;
        } catch (DataIntegrityViolationException e) {
            throw exceptionFactory.createAlreadyExistsException(
                    "Tree",
                    "code",
                    trees.stream().map(Tree::getCode).toList(),
                    TreeError.TREE_ALREADY_EXISTS
            );
        }
    }

    private void saveMeasurementsBatch(List<Tree> trees, List<Integer> validIndices, List<TreeCreateDTO> requests) {
        List<MeasurementTree> measurements = new ArrayList<>();
        for (int i = 0; i < trees.size() && i < validIndices.size(); i++) {
            TreeCreateDTO request = requests.get(validIndices.get(i));
            MeasurementTree measurement = treeUtils.buildMeasurement(request, trees.get(i));
            if (measurement != null) {
                measurements.add(measurement);
            }
        }

        if (!measurements.isEmpty()) {
            try {
                for (int i = 0; i < measurements.size(); i += SAVE_BATCH_SIZE) {
                    List<MeasurementTree> batch = measurements.subList(i, Math.min(i + SAVE_BATCH_SIZE, measurements.size()));
                    measurementRepository.saveAll(batch);
                }
                measurementRepository.flush(); // Flush cuối
            } catch (DataIntegrityViolationException e) {
                throw exceptionFactory.createAlreadyExistsException(
                        "MeasurementTree",
                        "measurementId",
                        measurements.stream().map(MeasurementTree::getMeasurementId).toList(),
                        MeasurementTreeError.MEASUREMENT_CREATION_FAILED
                );
            }
        }
    }

    private void updateTreeFields(Tree tree, TreeUpdateDTO request) {
        if (request.getLocalName() != null) {
            Species species = speciesRepository.findByLocalName(request.getLocalName().toLowerCase())
                    .orElseThrow(() -> exceptionFactory.createNotFoundException(
                            "Species", "LocalName", request.getLocalName(), SpeciesError.SPECIES_NOT_FOUND
                    ));
            tree.setSpecies(species);
        }

        if (request.getZoneName() != null) {
            Zone zone = zoneRepository.findByZoneName(request.getZoneName().toLowerCase())
                    .orElseThrow(() -> exceptionFactory.createNotFoundException(
                            "Zone", "ZoneName", request.getZoneName(), ZoneError.ZONE_NOT_FOUND
                    ));
            tree.setZone(zone);
        }

        if (request.getLatitude() != null && request.getLongitude() != null) {
            if (request.getLatitude() < -90 || request.getLatitude() > 90 ||
                    request.getLongitude() < -180 || request.getLongitude() > 180) {
                throw exceptionFactory.createValidationException(
                        "Tree", "coordinates",
                        request.getLatitude() + "," + request.getLongitude(),
                        ValidationError.COORDINATES_OUT_OF_RANGE
                );
            }

            String coords = treeUtils.formatCoordinate(request.getLatitude(), request.getLongitude());
            if (treeRepository.existsByCoordinates(request.getLatitude(), request.getLongitude()) &&
                    !coords.equals(treeUtils.formatCoordinate(tree.getLatitude(), tree.getLongitude()))) {
                throw exceptionFactory.createAlreadyExistsException(
                        "Tree",
                        "coordinates",
                        request.getLatitude() + "," + request.getLongitude(),
                        TreeError.TREE_ALREADY_EXISTS
                );
            }

            tree.setLatitude(request.getLatitude());
            tree.setLongitude(request.getLongitude());
        }

        if (request.getImgUrl() != null) {
            tree.setImgUrl(request.getImgUrl().trim().isEmpty() ? null : treeUtils.convertGoogleDriveUrl(request.getImgUrl()));
        }

        if (request.getPlantedDate() != null) {
            if (request.getPlantedDate().isAfter(LocalDate.now())) {
                throw exceptionFactory.createValidationException(
                        "Tree",
                        "plantedDate",
                        request.getPlantedDate(),
                        ValidationError.INVALID_PLANTED_DATE
                );
            }
            tree.setPlantedDate(request.getPlantedDate());
        }

        if (request.getDeletedAt() != null) {
            tree.setDeletedAt(request.getDeletedAt());
        }
    }
}
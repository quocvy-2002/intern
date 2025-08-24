package com.example.demo.service.tree;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.tree.TreeMapper;
import com.example.demo.model.dto.tree.tree.BatchFetchResult;
import com.example.demo.model.dto.tree.tree.TreeCreateDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.model.dto.tree.tree.TreeMeasurementDTO;
import com.example.demo.model.entity.tree.Measurement;
import com.example.demo.model.entity.tree.Species;
import com.example.demo.model.entity.tree.Tree;
import com.example.demo.model.entity.tree.Zone;
import com.example.demo.repository.tree.MeasurementRepository;
import com.example.demo.repository.tree.SpeciesRepository;
import com.example.demo.repository.tree.TreeRepository;
import com.example.demo.repository.tree.ZoneRepository;
import com.example.demo.service.excel.ExcelHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeService {
    TreeRepository treeRepository;
    TreeMapper treeMapper;
    SpeciesRepository speciesRepository;
    ZoneRepository zoneRepository;
    ExcelHelper excelHelper;
    MeasurementRepository measurementRepository;

    GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    public String generateTreeCode(String zoneName, long currentCount) {
        return String.format("%s_T%04d", zoneName, currentCount + 1);
    }

    public TreeDTO createTree(TreeCreateDTO request) {
        return createTreeList(Collections.singletonList(request)).get(0);
    }

    public List<TreeDTO> getAllTrees() {
        return treeRepository.findAll()
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    @Transactional
    public List<TreeDTO> createTreeList(List<TreeCreateDTO> requests) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        BatchFetchResult batchData = fetchBatchData(requests);
        List<Tree> treesToSave = buildTrees(requests, batchData);
        List<Tree> savedTrees = treeRepository.saveAll(treesToSave);
        return savedTrees.stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    @Transactional
    public List<TreeDTO> createTreesWithMeasurements(MultipartFile file) {
        List<TreeMeasurementDTO> treeMeasurementList = excelHelper.excelToTreeMeasurements(file);

        if (treeMeasurementList.isEmpty()) {
            return Collections.emptyList();
        }

        List<TreeCreateDTO> treeRequests = treeMeasurementList.stream()
                .map(TreeMeasurementDTO::getTree)
                .toList();

        List<TreeDTO> savedTrees = createTreeList(treeRequests);

        createMeasurementsForTrees(savedTrees, treeMeasurementList);

        return savedTrees;
    }

    public List<TreeDTO> createTreesFromExcel(MultipartFile file) {
        List<TreeCreateDTO> treeRequests = excelHelper.excelToTrees(file);
        return createTreeList(treeRequests);
    }

    // ==================== HELPER METHODS ====================

    private BatchFetchResult fetchBatchData(List<TreeCreateDTO> requests) {
        Set<UUID> speciesIds = requests.stream()
                .map(TreeCreateDTO::getSpeciesId)
                .collect(Collectors.toSet());

        Set<UUID> zoneIds = requests.stream()
                .map(TreeCreateDTO::getZoneId)
                .collect(Collectors.toSet());

        Map<UUID, Species> speciesMap = speciesRepository.findAllById(speciesIds)
                .stream()
                .collect(Collectors.toMap(Species::getSpeciesId, Function.identity()));

        Map<UUID, Zone> zoneMap = zoneRepository.findAllById(zoneIds)
                .stream()
                .collect(Collectors.toMap(Zone::getZoneId, Function.identity()));
        validateEntitiesExist(speciesIds, zoneIds, speciesMap, zoneMap);

        Map<UUID, Long> zoneCountMap = zoneIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        treeRepository::countByZoneId
                ));

        return new BatchFetchResult(speciesMap, zoneMap, zoneCountMap);
    }

    private void validateEntitiesExist(Set<UUID> speciesIds, Set<UUID> zoneIds,
                                       Map<UUID, Species> speciesMap, Map<UUID, Zone> zoneMap) {
        if (speciesMap.size() != speciesIds.size()) {
            throw new AppException(ErrorCode.SPECIES_NOT_EXISTS);
        }
        if (zoneMap.size() != zoneIds.size()) {
            throw new AppException(ErrorCode.ZONE_NOT_EXISTS);
        }
    }

    private List<Tree> buildTrees(List<TreeCreateDTO> requests, BatchFetchResult batchData) {
        LocalDate now = LocalDate.now();
        Map<UUID, AtomicLong> zoneCounters = batchData.zoneCountMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new AtomicLong(entry.getValue())
                ));

        return requests.stream()
                .map(request -> buildSingleTree(request, batchData, zoneCounters, now))
                .toList();
    }

    private Tree buildSingleTree(TreeCreateDTO request, BatchFetchResult batchData,
                                 Map<UUID, AtomicLong> zoneCounters, LocalDate now) {
        Species species = batchData.speciesMap.get(request.getSpeciesId());
        Zone zone = batchData.zoneMap.get(request.getZoneId());

        long currentCount = zoneCounters.get(zone.getZoneId()).getAndIncrement();
        LocalDateTime nows = LocalDateTime.now();
        Tree tree = treeMapper.toTree(request);
        tree.setSpecies(species);
        tree.setZone(zone);
        tree.setCode(generateTreeCode(zone.getZoneName(), currentCount));
        tree.setGeom(createPoint(request.getLongitude(), request.getLatitude()));
        tree.setCreatedAt(nows);

        return tree;
    }

    private Point createPoint(double longitude, double latitude) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    private void createMeasurementsForTrees(List<TreeDTO> savedTrees, List<TreeMeasurementDTO> treeMeasurementList) {
        List<Measurement> measurements = IntStream.range(0, savedTrees.size())
                .mapToObj(i -> createMeasurement(savedTrees.get(i), treeMeasurementList.get(i)))
                .toList();

        measurementRepository.saveAll(measurements);
    }

    private Measurement createMeasurement(TreeDTO treeDTO, TreeMeasurementDTO tmDTO) {
        return Measurement.builder()
                .measurementId(UUID.randomUUID())
                .tree(treeMapper.toTree(treeDTO))
                .dbhCm(tmDTO.getDbhCm())
                .heightM(tmDTO.getHeightM())
                .canopyDiameterM(tmDTO.getCanopyDiameterM())
                .healthStatus(tmDTO.getHealthStatus())
                .measuredAt(tmDTO.getMeasuredAt())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ==================== QUERY METHODS (unchanged) ====================

    public List<TreeDTO> getTreesByZoneId(UUID zoneId) {
        return treeRepository.findByZoneId(zoneId)
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    public List<TreeDTO> getTreesBySpeciesId(UUID speciesId) {
        return treeRepository.findBySpeciesId(speciesId)
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    public List<TreeDTO> getTreesByPlantedDate(LocalDate plantedDate) {
        return treeRepository.findByPlantedDate(plantedDate)
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    public TreeDTO getTreeByCode(String code) {
        Tree tree = treeRepository.findByCode(code);
        if (tree == null) {
            throw new AppException(ErrorCode.TREE_NOT_EXISTS);
        }
        return treeMapper.toTreeDTO(tree);
    }

    public List<TreeDTO> searchTreesByCode(String codePattern) {
        return treeRepository.searchByCode(codePattern)
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    public List<TreeDTO> getTreesByPlantedDateRange(LocalDate start, LocalDate end) {
        return treeRepository.findByPlantedDateBetween(start, end)
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
    }

    public void deleteTree(UUID treeId) {
        Tree tree = treeRepository.findTreeById(treeId)
                .orElseThrow(() -> new AppException(ErrorCode.TREE_NOT_EXISTS));
        treeRepository.delete(tree);
    }

    // ==================== EXPORT METHODS ====================

    public Workbook exportAllTreesToExcel() {
        List<TreeDTO> trees = treeRepository.findAll()
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
        return excelHelper.exportTreesToExcel(trees);
    }

    public Workbook exportAllTreesWithMeasurementsToExcel() {
        List<TreeDTO> trees = treeRepository.findAll()
                .stream()
                .map(treeMapper::toTreeDTO)
                .toList();
        List<Measurement> measurements = measurementRepository.findAll();
        return excelHelper.exportTreesWithMeasurementsToExcel(trees, measurements);
    }
}
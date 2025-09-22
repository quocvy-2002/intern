package com.example.demo.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TreeUtils {
    private static final Set<String> VALID_HEALTH_STATUSES = Set.of("HEALTHY", "FAIR", "POOR", "DEAD");
    private static final Pattern[] FILE_ID_PATTERNS = {
            Pattern.compile("/file/d/([a-zA-Z0-9_-]{25,})"),
            Pattern.compile("[?&]id=([a-zA-Z0-9_-]{25,})"),
            Pattern.compile("/d/([a-zA-Z0-9_-]{25,})"),
            Pattern.compile("uc\\?.*id=([a-zA-Z0-9_-]{25,})")
    };
    private static final BigDecimal PI = BigDecimal.valueOf(Math.PI);
    private static final BigDecimal BIOMASS_COEFF = BigDecimal.valueOf(0.0509);
    private static final BigDecimal CARBON_RATIO = BigDecimal.valueOf(0.5);
    private static final BigDecimal CO2_RATIO = BigDecimal.valueOf(3.67);
    private static final BigDecimal O2_CO2_MOLAR_RATIO = BigDecimal.valueOf(0.727);

    TreeRepository treeRepository;
    SpeciesRepository speciesRepository;
    ZoneRepository zoneRepository;
    MeasurementService measurementService;
    ExceptionFactory exceptionFactory;

    public enum ValidationKey {
        VALID, SPECIES, ZONE, REQUEST, INDEX
    }

    private static class Coordinate {
        final double latitude;
        final double longitude;

        Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return Math.abs(that.latitude - latitude) < 0.000001 &&
                    Math.abs(that.longitude - longitude) < 0.000001;
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude);
        }
    }

    public void validateSingleRequest(TreeCreateDTO request, BatchFetchResult batchData, Set<Coordinate> usedCoordinates,
                                      Set<String> usedCodes, int index) {
        validateCoordinatesSafe(request, usedCoordinates, index);
        String localName = request.getLocalName().toLowerCase().trim();
        Species species = batchData.getSpeciesByName().get(localName);
        if (species == null) {
            throw exceptionFactory.createNotFoundException("Species", "localName", request.getLocalName(), SpeciesError.SPECIES_NOT_FOUND);
        }

        String zoneName = request.getZoneName().toLowerCase().trim();
        Zone zone = batchData.getZonesByName().get(zoneName);
        if (zone == null) {
            throw exceptionFactory.createNotFoundException("Zone", "zoneName", request.getZoneName(), ZoneError.ZONE_NOT_FOUND);
        }

        validateMeasurements(request, index);
        validateDates(request, index);
        validateHealthStatus(request, index);

    }

    public Map<Integer, Map<String, Object>> validateRequestsBatch(List<TreeCreateDTO> requests, BatchFetchResult batchData) {
        Set<Coordinate> batchCoordinates = ConcurrentHashMap.newKeySet(); // Thread-safe
        batchCoordinates.addAll(batchData.getExistingCoordinates().stream()
                .map(coord -> {
                    String[] parts = coord.split(",");
                    return new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                })
                .collect(Collectors.toSet()));

        Set<String> batchCodes = ConcurrentHashMap.newKeySet(); // Thread-safe
        batchCodes.addAll(batchData.getExistingCodes());

        Map<Integer, Map<String, Object>> results = new HashMap<>(); // Không cần concurrent vì sequential

        for (int i = 0; i < requests.size(); i++) { // Sequential loop để tránh overhead parallel
            TreeCreateDTO request = requests.get(i);
            Map<String, Object> result = new HashMap<>();
            result.put(ValidationKey.INDEX.name(), i);
            result.put(ValidationKey.REQUEST.name(), request);
            result.put(ValidationKey.VALID.name(), false);

            try {
                validateSingleRequest(request, batchData, batchCoordinates, batchCodes, i);
                result.put(ValidationKey.SPECIES.name(), batchData.getSpeciesByName().get(request.getLocalName().toLowerCase().trim()));
                result.put(ValidationKey.ZONE.name(), batchData.getZonesByName().get(request.getZoneName().toLowerCase().trim()));
                result.put(ValidationKey.VALID.name(), true);

                // Không generate code ở đây, di chuyển sang buildTree
                batchCoordinates.add(new Coordinate(request.getLatitude(), request.getLongitude())); // Concurrent safe
            } catch (RuntimeException e) {
                System.err.println("Validation failed for tree at index " + i +
                        " - Species: " + request.getLocalName() +
                        ", Zone: " + request.getZoneName() +
                        ", Reason: " + e.getMessage());
            }

            results.put(i, result);
        }

        return results;
    }

    @Cacheable("species")
    public Map<String, Species> getSpeciesByName() {
        return speciesRepository.findAll()
                .stream()
                .collect(Collectors.toConcurrentMap(
                        s -> s.getLocalName().toLowerCase().trim(),
                        s -> s,
                        (existing, replacement) -> existing));
    }

    @Cacheable("zones")
    public Map<String, Zone> getZonesByName() {
        return zoneRepository.findAll()
                .stream()
                .collect(Collectors.toConcurrentMap(
                        z -> z.getZoneName().toLowerCase().trim(),
                        z -> z,
                        (existing, replacement) -> existing));
    }

    public BatchFetchResult fetchBatchData() {
        Map<String, Species> speciesByName = getSpeciesByName();
        Map<String, Zone> zonesByName = getZonesByName();

        List<Object[]> treeData = treeRepository.findAllCoordinatesAndCodes();
        Set<String> existingCoordinates = ConcurrentHashMap.newKeySet();
        Set<String> existingCodes = ConcurrentHashMap.newKeySet();

        treeData.parallelStream().forEach(data -> {
            existingCoordinates.add(formatCoordinate((Double) data[0], (Double) data[1]));
            existingCodes.add((String) data[2]);
        });

        Map<Long, AtomicLong> zoneCounters = treeRepository.findMaxIndexByAllZones()
                .entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        e -> new AtomicLong(e.getValue() != null ? e.getValue() : 0L)));

        return new BatchFetchResult(speciesByName, zonesByName, zoneCounters, existingCodes, existingCoordinates);
    }

    public void updateBatchDataAfterSave(BatchFetchResult currentBatchData, List<Tree> savedTrees,
                                         List<TreeCreateDTO> requests, List<Integer> validIndices) {

        for (int i = 0; i < savedTrees.size() && i < validIndices.size(); i++) {
            TreeCreateDTO request = requests.get(validIndices.get(i));
            currentBatchData.getExistingCoordinates().add(formatCoordinate(request.getLatitude(), request.getLongitude()));
            currentBatchData.getExistingCodes().add(savedTrees.get(i).getCode());
        }

    }

    public Tree buildTree(TreeCreateDTO request, Species species, Zone zone, Map<Long, AtomicLong> counters, String code) {
        String imgUrl = null;
        if (request.getImgUrl() != null && !request.getImgUrl().trim().isEmpty()) {
            imgUrl = convertGoogleDriveUrl(request.getImgUrl());
        }

        return Tree.builder()
                .species(species)
                .zone(zone)
                .code(code) // Sử dụng code đã generate
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imgUrl(imgUrl)
                .plantedDate(Optional.ofNullable(request.getPlantedDate()).orElse(LocalDate.now()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public MeasurementTree buildMeasurement(TreeCreateDTO request, Tree tree) {
        if (request == null || tree == null) {
            return null;
        }

        Species species = tree.getSpecies();
        if (species == null) {
            return null;
        }

        MeasurementTree.MeasurementTreeBuilder builder = MeasurementTree.builder()
                .tree(tree)
                .girthCm(request.getGirthCm())
                .heightM(request.getHeightM())
                .canopyDiameterM(request.getCanopyDiameterM())
                .healthStatus(request.getHealthStatus())
                .measuredAt(request.getMeasuredAt())
                .createdAt(LocalDateTime.now());

        calculateDerivedMeasurements(request, species, builder);
        return builder.build();
    }

    public Tree buildTreeFromValidation(Map<String, Object> validationResult, BatchFetchResult batchData) {
        TreeCreateDTO request = (TreeCreateDTO) validationResult.get(ValidationKey.REQUEST.name());
        Species species = (Species) validationResult.get(ValidationKey.SPECIES.name());
        Zone zone = (Zone) validationResult.get(ValidationKey.ZONE.name());

        // Generate code ở đây, sau validate
        String code = generateTreeCodeThreadSafe(zone, batchData.getZoneCounters());

        // Kiểm tra code existence (di chuyển từ validate)
        if (batchData.getExistingCodes().contains(code)) {
            throw exceptionFactory.createAlreadyExistsException("Tree", "code", code, TreeError.TREE_ALREADY_EXISTS);
        }

        return buildTree(request, species, zone, batchData.getZoneCounters(), code); // Thêm param code
    }

    public String formatCoordinate(Double lat, Double lng) {
        if (lat == null || lng == null) return "";
        return String.format("%.6f,%.6f", lat, lng);
    }

    public String generateTreeCodeThreadSafe(Zone zone, Map<Long, AtomicLong> counters) {
        String prefix = zone.getZoneName().trim().replaceAll("\\s+", "_");
        AtomicLong counter = counters.computeIfAbsent(zone.getZoneId(), k -> new AtomicLong(0));
        return String.format("%s_T%04d", prefix, counter.incrementAndGet());
    }

    public String convertGoogleDriveUrl(String url) {
        if (url == null || url.trim().isEmpty() || !url.contains("drive.google.com")) {
            return url;
        }

        String fileId = extractFileId(url);
        return fileId != null ? "https://lh3.googleusercontent.com/d/" + fileId + "=s0" : url;
    }

    private void validateCoordinatesSafe(TreeCreateDTO request, Set<Coordinate> usedCoordinates, int index) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw exceptionFactory.createValidationException(
                    "Tree", "coordinates", null, ValidationError.COORDINATES_REQUIRED);
        }

        if (request.getLatitude() < -90 || request.getLatitude() > 90 ||
                request.getLongitude() < -180 || request.getLongitude() > 180) {
            throw exceptionFactory.createValidationException(
                    "Tree", "coordinates",
                    request.getLatitude() + "," + request.getLongitude(),
                    ValidationError.COORDINATES_OUT_OF_RANGE);
        }

        Coordinate coordinate = new Coordinate(request.getLatitude(), request.getLongitude());
        if (usedCoordinates.contains(coordinate)) {
            throw exceptionFactory.createAlreadyExistsException(
                    "Tree", "coordinates",
                    request.getLatitude() + "," + request.getLongitude(),
                    TreeError.TREE_ALREADY_EXISTS);
        }
    }

    private void validateMeasurements(TreeCreateDTO request, int index) {
        if (request.getGirthCm() != null && request.getGirthCm().compareTo(BigDecimal.ZERO) <= 0) {
            throw exceptionFactory.createValidationException(
                    "Tree", "girthCm", request.getGirthCm(), ValidationError.INVALID_GIRTH);
        }
        if (request.getHeightM() != null && request.getHeightM().compareTo(BigDecimal.ZERO) <= 0) {
            throw exceptionFactory.createValidationException(
                    "Tree", "heightM", request.getHeightM(), ValidationError.INVALID_HEIGHT);
        }
        if (request.getCanopyDiameterM() != null && request.getCanopyDiameterM().compareTo(BigDecimal.ZERO) <= 0) {
            throw exceptionFactory.createValidationException(
                    "Tree", "canopyDiameterM", request.getCanopyDiameterM(),
                    ValidationError.INVALID_CANOPY_DIAMETER);
        }
    }

    private void validateDates(TreeCreateDTO request, int index) {
        if (request.getPlantedDate() != null && request.getPlantedDate().isAfter(LocalDate.now())) {
            throw exceptionFactory.createValidationException(
                    "Tree", "plantedDate", request.getPlantedDate(),
                    ValidationError.INVALID_PLANTED_DATE);
        }
    }

    private void validateHealthStatus(TreeCreateDTO request, int index) {
        if (request.getHealthStatus() != null &&
                !VALID_HEALTH_STATUSES.contains(request.getHealthStatus().name().toUpperCase())) {
            throw exceptionFactory.createValidationException(
                    "Tree", "healthStatus", request.getHealthStatus(),
                    ValidationError.INVALID_HEALTH_STATUS);
        }
    }

    private void calculateDerivedMeasurements(TreeCreateDTO request, Species species,
                                              MeasurementTree.MeasurementTreeBuilder builder) {
        BigDecimal leafArea = calculateLeafArea(request.getCanopyDiameterM(), species.getLai());
        builder.leafAreaM2(leafArea);

        if (hasValidBiomassInputs(request, species)) {
            BiomassCalculations biomass = calculateBiomassMetrics(request, species);
            builder.biomassKg(biomass.biomass)
                    .carbonKg(biomass.carbon)
                    .co2AbsorbedKg(biomass.co2)
                    .o2ReleasedKg(biomass.o2);
        } else {
            builder.biomassKg(BigDecimal.ZERO)
                    .carbonKg(BigDecimal.ZERO)
                    .co2AbsorbedKg(BigDecimal.ZERO)
                    .o2ReleasedKg(BigDecimal.ZERO);
        }

        BigDecimal waterLoss = calculateWaterLoss(leafArea, species.getPlantFactor());
        builder.waterLoss(waterLoss);
    }

    private BigDecimal calculateLeafArea(BigDecimal canopyDiameter, Double lai) {
        if (canopyDiameter == null || lai == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal radius = canopyDiameter.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP);
        BigDecimal canopyArea = PI.multiply(radius.pow(2));
        return canopyArea.multiply(BigDecimal.valueOf(lai)).setScale(4, RoundingMode.HALF_UP);
    }

    private boolean hasValidBiomassInputs(TreeCreateDTO request, Species species) {
        return request.getGirthCm() != null &&
                request.getHeightM() != null &&
                species.getWoodDensity() != null;
    }

    private BiomassCalculations calculateBiomassMetrics(TreeCreateDTO request, Species species) {
        BigDecimal dbhCm = request.getGirthCm().divide(PI, 10, RoundingMode.HALF_UP);
        BigDecimal dbhSquared = dbhCm.pow(2);
        BigDecimal term = species.getWoodDensity().multiply(dbhSquared).multiply(request.getHeightM());

        BigDecimal biomass = BIOMASS_COEFF.multiply(term).setScale(4, RoundingMode.HALF_UP);
        BigDecimal carbon = biomass.multiply(CARBON_RATIO).setScale(4, RoundingMode.HALF_UP);
        BigDecimal co2 = carbon.multiply(CO2_RATIO).setScale(4, RoundingMode.HALF_UP);
        BigDecimal o2 = co2.multiply(O2_CO2_MOLAR_RATIO).setScale(4, RoundingMode.HALF_UP);

        return new BiomassCalculations(biomass, carbon, co2, o2);
    }

    private BigDecimal calculateWaterLoss(BigDecimal leafArea, BigDecimal plantFactor) {
        if (leafArea.compareTo(BigDecimal.ZERO) <= 0 || plantFactor == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal plantArea = leafArea.multiply(leafArea).multiply(BigDecimal.valueOf(0.7854));
        return plantArea.multiply(BigDecimal.valueOf(0.623))
                .multiply(BigDecimal.valueOf(0.2).multiply(plantFactor))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private String extractFileId(String url) {
        for (Pattern pattern : FILE_ID_PATTERNS) {
            java.util.regex.Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static class BiomassCalculations {
        final BigDecimal biomass;
        final BigDecimal carbon;
        final BigDecimal co2;
        final BigDecimal o2;

        BiomassCalculations(BigDecimal biomass, BigDecimal carbon, BigDecimal co2, BigDecimal o2) {
            this.biomass = biomass;
            this.carbon = carbon;
            this.co2 = co2;
            this.o2 = o2;
        }
    }

}
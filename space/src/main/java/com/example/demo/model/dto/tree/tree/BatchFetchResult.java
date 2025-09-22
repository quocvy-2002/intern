package com.example.demo.model.dto.tree.tree;

import com.example.SmartBuildingBackend.model.entity.tree.Species;
import com.example.SmartBuildingBackend.model.entity.tree.Zone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A data transfer object that holds batch-fetched data for tree creation.
 * Contains mappings of species, zones, zone tree counts, and validated tree creation requests.
 * Enhanced with optimization fields for high-performance bulk operations.
 */
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchFetchResult {
    // Original fields for backward compatibility
    Map<Long, Species> speciesMap;
    Map<Long, Zone> zoneMap;
    Map<Long, Long> zoneCountMap;
    List<TreeCreateDTO> requestsWithIds;

    // New optimization fields
    Map<String, Species> speciesByName;
    Map<String, Zone> zonesByName;
    Map<Long, AtomicLong> zoneCounters;
    Set<String> existingCodes;
    Set<String> existingCoordinates;

    /**
     * Constructor for initializing with species and zone maps.
     * Initializes an empty zoneCountMap and requestsWithIds.
     *
     * @param speciesMap Mapping of species IDs to Species entities
     * @param zoneMap Mapping of zone IDs to Zone entities
     */
    public BatchFetchResult(Map<Long, Species> speciesMap, Map<Long, Zone> zoneMap) {
        this.speciesMap = speciesMap != null ? new HashMap<>(speciesMap) : new HashMap<>();
        this.zoneMap = zoneMap != null ? new HashMap<>(zoneMap) : new HashMap<>();
        this.zoneCountMap = new HashMap<>();
        this.requestsWithIds = Collections.emptyList();
        initializeOptimizationFields();
    }

    /**
     * Constructor for initializing with species, zone, and zone count maps.
     *
     * @param speciesMap Mapping of species IDs to Species entities
     * @param zoneMap Mapping of zone IDs to Zone entities
     * @param zoneCountMap Mapping of zone IDs to the maximum tree index in each zone
     */
    public BatchFetchResult(Map<Long, Species> speciesMap, Map<Long, Zone> zoneMap, Map<Long, Long> zoneCountMap) {
        this.speciesMap = speciesMap != null ? new HashMap<>(speciesMap) : new HashMap<>();
        this.zoneMap = zoneMap != null ? new HashMap<>(zoneMap) : new HashMap<>();
        this.zoneCountMap = zoneCountMap != null ? new HashMap<>(zoneCountMap) : new HashMap<>();
        this.requestsWithIds = Collections.emptyList();
        initializeOptimizationFields();
    }

    /**
     * Optimized constructor for high-performance bulk operations
     */
    public BatchFetchResult(Map<String, Species> speciesByName,
                            Map<String, Zone> zonesByName,
                            Map<Long, AtomicLong> zoneCounters,
                            Set<String> existingCodes,
                            Set<String> existingCoordinates) {
        this.speciesByName = speciesByName != null ? new HashMap<>(speciesByName) : new HashMap<>();
        this.zonesByName = zonesByName != null ? new HashMap<>(zonesByName) : new HashMap<>();
        this.zoneCounters = zoneCounters != null ? new HashMap<>(zoneCounters) : new HashMap<>();
        this.existingCodes = existingCodes != null ? new HashSet<>(existingCodes) : new HashSet<>();
        this.existingCoordinates = existingCoordinates != null ? new HashSet<>(existingCoordinates) : new HashSet<>();
        this.requestsWithIds = Collections.emptyList();

        // Build legacy maps for backward compatibility
        this.speciesMap = this.speciesByName.values().stream()
                .collect(Collectors.toMap(Species::getSpeciesId, Function.identity()));
        this.zoneMap = this.zonesByName.values().stream()
                .collect(Collectors.toMap(Zone::getZoneId, Function.identity()));
        this.zoneCountMap = this.zoneCounters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
    }

    /**
     * Initialize optimization fields from legacy data
     */
    private void initializeOptimizationFields() {
        // Build name-based maps for fast lookup
        if (speciesMap != null) {
            this.speciesByName = speciesMap.values().stream()
                    .collect(Collectors.toMap(
                            s -> s.getLocalName().toLowerCase(Locale.ROOT),
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        } else {
            this.speciesByName = new HashMap<>();
        }

        if (zoneMap != null) {
            this.zonesByName = zoneMap.values().stream()
                    .collect(Collectors.toMap(
                            z -> z.getZoneName().toLowerCase(Locale.ROOT),
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        } else {
            this.zonesByName = new HashMap<>();
        }

        // Convert Long counters to AtomicLong for thread safety
        if (zoneCountMap != null) {
            this.zoneCounters = zoneCountMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new AtomicLong(entry.getValue())
                    ));
        } else {
            this.zoneCounters = new HashMap<>();
        }

        // Initialize empty sets if not provided
        this.existingCodes = new HashSet<>();
        this.existingCoordinates = new HashSet<>();
    }

    /**
     * Creates a new instance with the provided requests list.
     *
     * @param requests List of validated TreeCreateDTO objects
     * @return A new BatchFetchResult with the updated requests
     */
    public BatchFetchResult withRequests(List<TreeCreateDTO> requests) {
        return toBuilder()
                .requestsWithIds(requests != null ? List.copyOf(requests) : Collections.emptyList())
                .build();
    }

    /**
     * Set existing codes for optimization
     */
    public BatchFetchResult withExistingCodes(Set<String> codes) {
        this.existingCodes = codes != null ? new HashSet<>(codes) : new HashSet<>();
        return this;
    }

    /**
     * Set existing coordinates for optimization
     */
    public BatchFetchResult withExistingCoordinates(Set<String> coordinates) {
        this.existingCoordinates = coordinates != null ? new HashSet<>(coordinates) : new HashSet<>();
        return this;
    }

    /**
     * Validates the integrity of the data in this object.
     * Throws IllegalStateException if critical data is missing or invalid.
     */
    public void validate() {
        // Check legacy fields first
        if ((speciesMap == null || speciesMap.isEmpty()) &&
                (speciesByName == null || speciesByName.isEmpty())) {
            throw new IllegalStateException("Species data cannot be null or empty");
        }
        if ((zoneMap == null || zoneMap.isEmpty()) &&
                (zonesByName == null || zonesByName.isEmpty())) {
            throw new IllegalStateException("Zone data cannot be null or empty");
        }
        if (zoneCountMap == null && zoneCounters == null) {
            throw new IllegalStateException("Zone count data cannot be null");
        }
    }

    // Getters and Setters for all fields
    public Map<Long, Species> getSpeciesMap() {
        return speciesMap;
    }

    public void setSpeciesMap(Map<Long, Species> speciesMap) {
        this.speciesMap = speciesMap;
    }

    public Map<Long, Zone> getZoneMap() {
        return zoneMap;
    }

    public void setZoneMap(Map<Long, Zone> zoneMap) {
        this.zoneMap = zoneMap;
    }

    public Map<Long, Long> getZoneCountMap() {
        return zoneCountMap;
    }

    public void setZoneCountMap(Map<Long, Long> zoneCountMap) {
        this.zoneCountMap = zoneCountMap;
    }

    public List<TreeCreateDTO> getRequestsWithIds() {
        return requestsWithIds;
    }

    public void setRequestsWithIds(List<TreeCreateDTO> requestsWithIds) {
        this.requestsWithIds = requestsWithIds;
    }

    public Map<String, Species> getSpeciesByName() {
        return speciesByName;
    }

    public void setSpeciesByName(Map<String, Species> speciesByName) {
        this.speciesByName = speciesByName;
    }

    public Map<String, Zone> getZonesByName() {
        return zonesByName;
    }

    public void setZonesByName(Map<String, Zone> zonesByName) {
        this.zonesByName = zonesByName;
    }

    public Map<Long, AtomicLong> getZoneCounters() {
        return zoneCounters;
    }

    public void setZoneCounters(Map<Long, AtomicLong> zoneCounters) {
        this.zoneCounters = zoneCounters;
    }

    public Set<String> getExistingCodes() {
        return existingCodes;
    }

    public void setExistingCodes(Set<String> existingCodes) {
        this.existingCodes = existingCodes;
    }

    public Set<String> getExistingCoordinates() {
        return existingCoordinates;
    }

    public void setExistingCoordinates(Set<String> existingCoordinates) {
        this.existingCoordinates = existingCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchFetchResult that = (BatchFetchResult) o;
        return Objects.equals(speciesMap, that.speciesMap) &&
                Objects.equals(zoneMap, that.zoneMap) &&
                Objects.equals(zoneCountMap, that.zoneCountMap) &&
                Objects.equals(requestsWithIds, that.requestsWithIds) &&
                Objects.equals(speciesByName, that.speciesByName) &&
                Objects.equals(zonesByName, that.zonesByName) &&
                Objects.equals(existingCodes, that.existingCodes) &&
                Objects.equals(existingCoordinates, that.existingCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speciesMap, zoneMap, zoneCountMap, requestsWithIds,
                speciesByName, zonesByName, existingCodes, existingCoordinates);
    }

    @Override
    public String toString() {
        return "BatchFetchResult{" +
                "speciesCount=" + (speciesMap != null ? speciesMap.size() : 0) +
                ", zoneCount=" + (zoneMap != null ? zoneMap.size() : 0) +
                ", requestCount=" + (requestsWithIds != null ? requestsWithIds.size() : 0) +
                ", existingCodesCount=" + (existingCodes != null ? existingCodes.size() : 0) +
                ", existingCoordinatesCount=" + (existingCoordinates != null ? existingCoordinates.size() : 0) +
                '}';
    }
}
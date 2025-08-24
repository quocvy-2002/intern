package com.example.demo.model.dto.tree.tree;

import com.example.demo.model.entity.tree.Species;
import com.example.demo.model.entity.tree.Zone;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class BatchFetchResult {
    public final Map<UUID, Species> speciesMap;
    public final Map<UUID, Zone> zoneMap;
    public final Map<UUID, Long> zoneCountMap;
}
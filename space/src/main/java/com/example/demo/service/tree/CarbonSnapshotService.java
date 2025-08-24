package com.example.demo.service.tree;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.tree.CarbonSnapshotMapper;
import com.example.demo.model.dto.tree.CarbonSnapshotDTO;
import com.example.demo.model.dto.tree.CarbonTreeDTO;
import com.example.demo.model.entity.tree.CarbonSnapshot;
import com.example.demo.model.entity.tree.Tree;
import com.example.demo.model.entity.tree.Zone;
import com.example.demo.repository.tree.CarbonSnapshotRepository;
import com.example.demo.repository.tree.TreeRepository;
import com.example.demo.repository.tree.ZoneRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CarbonSnapshotService {
    TreeRepository treeRepository;
    CarbonTreeService carbonTreeService;
    CarbonSnapshotRepository carbonSnapshotRepository;
    ZoneRepository zoneRepository;
    CarbonSnapshotMapper carbonSnapshotMapper;

    @Transactional
    public CarbonSnapshotDTO generateCarbonSnapshot(UUID zoneId) {
        List<Tree> trees = treeRepository.findByZoneZoneIdAndDeletedAtIsNull(zoneId);
        if (trees.isEmpty()) {
            throw new AppException(ErrorCode.TREE_NOT_EXISTS);
        }

        BigDecimal totalCarbonKg = BigDecimal.ZERO;
        int treeCount = 0;
        for (Tree tree : trees) {
            CarbonTreeDTO carbonTreeDto = carbonTreeService.calculateCarbonForTree(tree.getTreeId());
            totalCarbonKg = totalCarbonKg.add(carbonTreeDto.getCarbonKg());
            treeCount++;
        }
        CarbonSnapshot snapshot = CarbonSnapshot.builder()
                .snapshotId(UUID.randomUUID())
                .zone(zoneRepository.findByZoneId(zoneId)
                        .orElseThrow(() -> new AppException(ErrorCode.ZONE_NOT_EXISTS)))
                .totalCarbonKg(totalCarbonKg.setScale(2, RoundingMode.HALF_UP))
                .treeCount(treeCount)
                .calculatedAt(LocalDateTime.now())
                .build();
        snapshot = carbonSnapshotRepository.save(snapshot);
        CarbonSnapshotDTO dto = carbonSnapshotMapper.toDto(snapshot);
        dto.setTotalCo2Kg(carbonTreeService.convertToCO2(totalCarbonKg).setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 12AM daily
    @Transactional
    public void generateAllCarbonSnapshots() {
        List<Zone> zones = zoneRepository.findAll();
        for (Zone zone : zones) {
            if (zone.getIsActive()) {
                generateCarbonSnapshot(zone.getZoneId());
            }
        }
    }
}
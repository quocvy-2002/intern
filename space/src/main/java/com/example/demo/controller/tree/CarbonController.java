package com.example.demo.controller.tree;


import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.tree.CarbonSnapshotDTO;
import com.example.demo.model.dto.tree.CarbonTreeDTO;
import com.example.demo.service.tree.CarbonSnapshotService;
import com.example.demo.service.tree.CarbonTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/carbon")
@RequiredArgsConstructor
public class CarbonController {

    private final CarbonTreeService carbonTreeService;
    private final CarbonSnapshotService carbonSnapshotService;

    @GetMapping("/tree/{treeId}")
    public ApiResponse<CarbonTreeDTO> getCarbonForTree(@PathVariable UUID treeId) {
        return ApiResponse.<CarbonTreeDTO>builder()
                .result(carbonTreeService.calculateCarbonForTree(treeId))
                .build();
    }

    @GetMapping("/zone/{zoneId}")
    public ApiResponse<CarbonSnapshotDTO> getCarbonSnapshot(@PathVariable UUID zoneId) {
        return ApiResponse.<CarbonSnapshotDTO>builder()
                .result(carbonSnapshotService.generateCarbonSnapshot(zoneId))
                .build();
    }
}
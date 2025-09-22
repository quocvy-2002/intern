package com.example.demo.controller.tree;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import com.example.SmartBuildingBackend.model.dto.tree.CarbonSummary;
import com.example.SmartBuildingBackend.model.dto.tree.CarbonSummaryByZone;
import com.example.SmartBuildingBackend.model.dto.tree.SpeciesContribution;
import com.example.SmartBuildingBackend.service.tree.CarbonSnapshotService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/carbon")
@RequiredArgsConstructor
public class CarbonController {

    CarbonSnapshotService carbonSnapshotService;

    // ========== READ ==========
    @GetMapping("/summarize")
    public ApiResponse<CarbonSummary> summarizeByYear() {
        return ApiResponse.<CarbonSummary>builder()
                .result(carbonSnapshotService.summarize())
                .build();
    }

    @GetMapping("/summarize/year/{year}/zone")
    public ApiResponse<List<CarbonSummaryByZone>> summarizeByYearAndZone(@PathVariable int year) {
        return ApiResponse.<List<CarbonSummaryByZone>>builder()
                .result(carbonSnapshotService.summarizeByYearAndZone(year))
                .build();
    }

    @GetMapping("/top/species/o2")
    public ApiResponse<List<SpeciesContribution>> topSpeciesByO2(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<SpeciesContribution>>builder()
                .result(carbonSnapshotService.topSpeciesByO2(limit))
                .build();
    }

    @GetMapping("/top/species/leaf-area")
    public ApiResponse<List<SpeciesContribution>> topSpeciesByLeafArea(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<SpeciesContribution>>builder()
                .result(carbonSnapshotService.topSpeciesByLeafArea(limit))
                .build();
    }

    @GetMapping("/top/species/co2")
    public ApiResponse<List<SpeciesContribution>> topSpeciesByCO2(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<SpeciesContribution>>builder()
                .result(carbonSnapshotService.topSpeciesByCO2(limit))
                .build();
    }
}
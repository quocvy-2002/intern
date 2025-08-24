package com.example.demo.controller.tree;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.tree.tree.TreeCreateDTO;
import com.example.demo.model.dto.tree.tree.TreeDTO;
import com.example.demo.service.tree.TreeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/trees")
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;

    // ========== CREATE ==========
    @PostMapping
    public ApiResponse<TreeDTO> createTree(@RequestBody TreeCreateDTO request) {
        return ApiResponse.<TreeDTO>builder()
                .result(treeService.createTree(request))
                .build();
    }

    @PostMapping("/batch")
    public ApiResponse<List<TreeDTO>> createTreeList(@RequestBody List<TreeCreateDTO> requests) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.createTreeList(requests))
                .build();
    }

    @PostMapping("/import/excel")
    public ApiResponse<List<TreeDTO>> importTreesFromExcel(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.createTreesFromExcel(file))
                .build();
    }

    @PostMapping("/import/excel-with-measurements")
    public ApiResponse<List<TreeDTO>> importTreesWithMeasurements(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.createTreesWithMeasurements(file))
                .build();
    }

    // ========== READ ==========
    @GetMapping("/zone/{zoneId}")
    public ApiResponse<List<TreeDTO>> getTreesByZone(@PathVariable UUID zoneId) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getTreesByZoneId(zoneId))
                .build();
    }

    @GetMapping("/species/{speciesId}")
    public ApiResponse<List<TreeDTO>> getTreesBySpecies(@PathVariable UUID speciesId) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getTreesBySpeciesId(speciesId))
                .build();
    }

    @GetMapping("/planted-date/{plantedDate}")
    public ApiResponse<List<TreeDTO>> getTreesByPlantedDate(@PathVariable String plantedDate) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getTreesByPlantedDate(LocalDate.parse(plantedDate)))
                .build();
    }

    @GetMapping("/code/{code}")
    public ApiResponse<TreeDTO> getTreeByCode(@PathVariable String code) {
        return ApiResponse.<TreeDTO>builder()
                .result(treeService.getTreeByCode(code))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<TreeDTO>> searchTreesByCode(@RequestParam String codePattern) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.searchTreesByCode(codePattern))
                .build();
    }

    @GetMapping("/planted-date-range")
    public ApiResponse<List<TreeDTO>> getTreesByPlantedDateRange(@RequestParam String start,
                                                                 @RequestParam String end) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getTreesByPlantedDateRange(LocalDate.parse(start), LocalDate.parse(end)))
                .build();
    }

    // ========== DELETE ==========
    @DeleteMapping("/{treeId}")
    public ApiResponse<Void> deleteTree(@PathVariable UUID treeId) {
        treeService.deleteTree(treeId);
        return ApiResponse.<Void>builder().build();
    }

    // ========== EXPORT ==========
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportAllTrees() throws IOException {
        Workbook workbook = treeService.exportAllTreesToExcel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trees.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    @GetMapping("/export/excel-with-measurements")
    public ResponseEntity<byte[]> exportAllTreesWithMeasurements() throws IOException {
        Workbook workbook = treeService.exportAllTreesWithMeasurementsToExcel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trees_with_measurements.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    @GetMapping
    public ApiResponse<List<TreeDTO>> getAllTrees() {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getAllTrees())
                .build();
    }
}
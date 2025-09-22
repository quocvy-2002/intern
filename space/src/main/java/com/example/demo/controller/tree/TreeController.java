package com.example.demo.controller.tree;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import com.example.SmartBuildingBackend.model.dto.tree.tree.*;
import com.example.SmartBuildingBackend.service.tree.TreeService;
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

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/trees")
@RequiredArgsConstructor
public class TreeController {

    TreeService treeService;

    // ========== CREATE ==========
    @PostMapping
    public ApiResponse<TreeDTO> createTree(@RequestBody TreeCreateDTO request) {
        return ApiResponse.<TreeDTO>builder()
                .result(treeService.createTree(request))
                .build();
    }

    @PutMapping("/{code}")
    public ApiResponse<TreeDTO> updateTreeByCode(
            @PathVariable String code,
            @RequestBody TreeUpdateDTO request
    ) {
        return ApiResponse.<TreeDTO>builder()
                .result(treeService.updateTreeByCode(code, request))
                .build();
    }

//    @PostMapping("/tree-measurements")
//    public ApiResponse<TreeDTO> createTreeMeasurement(@RequestBody TreeCreateDTO request) {
//        return ApiResponse.<TreeDTO>builder()
//                .result(treeService.createTreeMeasurement(request))
//                .build();
//    }

    @PostMapping("/batch")
    public ApiResponse<List<TreeDTO>> createTreeList(@RequestBody List<TreeCreateDTO> requests) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.createTreeList(requests))
                .build();
    }

//    @PostMapping("/import")
//    public ApiResponse<List<TreeDTO>> importTreesFromExcel(@RequestParam("file") MultipartFile file) {
//        return ApiResponse.<List<TreeDTO>>builder()
//                .result(treeService.(file))
//                .build();
//    }

    @PostMapping("/import/excel-with-measurements")
    public ApiResponse<List<TreeDTO>> importTreesWithMeasurements(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.createTreesWithMeasurements(file))
                .build();
    }

    // ========== READ ==========
    @GetMapping("/zone/{zoneId}")
    public ApiResponse<List<TreeDTO>> getTreesByZone(@PathVariable Long zoneId) {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getTreesByZoneId(zoneId))
                .build();
    }

    @GetMapping("/species/{speciesId}")
    public ApiResponse<List<TreeDTO>> getTreesBySpecies(@PathVariable Long speciesId) {
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

    // ========== TREE HISTORY & MEASUREMENTS ==========
    @GetMapping("/{code}/measurements/times")
    public ApiResponse<List<String>> getTreeMeasurementTimes(
            @PathVariable String code
    ) {
        return ApiResponse.<List<String>>builder()
                .result(treeService.getTreesLocalDateTimes(code))
                .build();
    }

    // ========== DELETE ==========
    @DeleteMapping("/code/{code}")
    public ApiResponse<Void> deleteTree(@PathVariable String code) {
        treeService.deleteTree(code);
        return ApiResponse.<Void>builder().build();
    }

    // ========== EXPORT ==========
    @GetMapping("/export/excel-with-measurements")
    public ResponseEntity<byte[]> exportAllTreesWithMeasurements() throws IOException {
        Workbook workbook = treeService.exportAllTreesWithMeasurementsToExcel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"trees_with_measurements.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping
    public ApiResponse<List<TreeDTO>> getAllTrees() {
        return ApiResponse.<List<TreeDTO>>builder()
                .result(treeService.getAllTrees())
                .build();
    }

    @GetMapping("/map")
    public ApiResponse<List<TreeMapDTO>> getAllTreesMap() {
        return ApiResponse.<List<TreeMapDTO>>builder()
                .result(treeService.getAllTreeMap())
                .build();
    }

    @GetMapping("/species")
    public ApiResponse<List<TreeMapDTO>> getAllTreesMapBySpecies(
            @RequestParam String localName) {
        return ApiResponse.<List<TreeMapDTO>>builder()
                .result(treeService.getAllTreeMapBySpecies(localName))
                .build();
    }

}
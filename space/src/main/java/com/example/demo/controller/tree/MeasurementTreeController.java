package com.example.demo.controller.tree;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementDTO;
import com.example.SmartBuildingBackend.model.dto.tree.measurement.MeasurementUpdateDTO;
import com.example.SmartBuildingBackend.service.tree.MeasurementService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class MeasurementTreeController {

    MeasurementService measurementService;

    // ========== CREATE ==========
    @PostMapping("/tree/{treeId}")
    public ApiResponse<MeasurementDTO> createMeasurement(
            @PathVariable Long treeId,
            @Valid @RequestBody MeasurementUpdateDTO request) {
        return ApiResponse.<MeasurementDTO>builder()
                .result(measurementService.createMeasurement(treeId, request))
                .build();
    }

    // ========== UPDATE ==========
    @PutMapping("/{measurementId}")
    public ApiResponse<MeasurementDTO> updateMeasurement(
            @PathVariable Long measurementId,
            @Valid @RequestBody MeasurementUpdateDTO request) {
        return ApiResponse.<MeasurementDTO>builder()
                .result(measurementService.updateMeasurement(measurementId, request))
                .build();
    }

    // ========== READ ==========
    @GetMapping
    public ApiResponse<List<MeasurementDTO>> getAllMeasurements() {
        return ApiResponse.<List<MeasurementDTO>>builder()
                .result(measurementService.getAllMeasurements())
                .build();
    }

    @GetMapping("/tree/{treeId}")
    public ApiResponse<List<MeasurementDTO>> getMeasurementsByTree(@PathVariable Long treeId) {
        return ApiResponse.<List<MeasurementDTO>>builder()
                .result(measurementService.getMeasurementsByTreeId(treeId))
                .build();
    }

    @GetMapping("/date/{date}")
    public ApiResponse<List<MeasurementDTO>> getMeasurementsByDate(@PathVariable String date) {
        return ApiResponse.<List<MeasurementDTO>>builder()
                .result(measurementService.getMeasurementsByDate(LocalDate.parse(date)))
                .build();
    }

    @GetMapping("/date-range")
    public ApiResponse<List<MeasurementDTO>> getMeasurementsByDateRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return ApiResponse.<List<MeasurementDTO>>builder()
                .result(measurementService.getMeasurementsByDateRange(LocalDate.parse(start), LocalDate.parse(end)))
                .build();
    }

    // ========== EXPORT ==========
    @GetMapping("/export/all")
    public ResponseEntity<byte[]> exportAllMeasurementsToExcel() throws IOException {
        Workbook workbook = measurementService.exportAllMeasurementsToExcel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"all_measurements.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }

    @GetMapping("/export/tree/{treeId}")
    public ResponseEntity<byte[]> exportMeasurementsByTree(@PathVariable Long treeId) throws IOException {
        Workbook workbook = measurementService.exportMeasurementsByTreeToExcel(treeId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"measurements_" + treeId + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(out.toByteArray());
    }
}
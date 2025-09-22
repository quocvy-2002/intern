package com.example.demo.controller.tree;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneCreateDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneDTO;
import com.example.SmartBuildingBackend.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.SmartBuildingBackend.service.tree.ZoneService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/zones")
public class ZoneController {

    ZoneService zoneService;

    @PostMapping
    ApiResponse<ZoneDTO> createZone(@Valid @RequestBody ZoneCreateDTO request) {
        return ApiResponse.<ZoneDTO>builder()
                .result(zoneService.createZone(request))
                .build();
    }

    @GetMapping("/{zoneId}")
    public ApiResponse<ZoneDTO> getZone(@PathVariable Long zoneId) {
        return ApiResponse.<ZoneDTO>builder()
                .result(zoneService.getZoneById(zoneId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ZoneDTO>> getAllZones() {
        return ApiResponse.<List<ZoneDTO>>builder()
                .result(zoneService.getAllZones())
                .build();
    }

    @DeleteMapping("/{zoneId}")
    public void deleteZone(@PathVariable Long zoneId) {
        zoneService.deleteZone(zoneId);
    }

    @PutMapping("/{zoneId}")
    public ApiResponse<ZoneDTO> updateZone(
            @PathVariable Long zoneId,
            @Valid @RequestBody ZoneUpdateDTO request
    ) {
        return ApiResponse.<ZoneDTO>builder()
                .result(zoneService.updateZone(zoneId, request))
                .build();
    }

    @PostMapping("/import")
    public ApiResponse<List<ZoneDTO>> importZones(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<ZoneDTO>>builder()
                .result(zoneService.importZonesFromExcel(file))
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        ByteArrayInputStream in = zoneService.exportZoneToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=species.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }
}

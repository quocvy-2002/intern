package com.example.demo.controller.tree;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.tree.zone.ZoneCreateDTO;
import com.example.demo.model.dto.tree.zone.ZoneDTO;
import com.example.demo.model.dto.tree.zone.ZoneUpdateDTO;
import com.example.demo.service.tree.ZoneService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
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
    public ApiResponse<ZoneDTO> getZone(@PathVariable UUID zoneId) {
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
    public void deleteZone(@PathVariable UUID zoneId) {
        zoneService.deleteZone(zoneId);
    }

    @PutMapping("/{zoneId}")
    public ApiResponse<ZoneDTO> updateZone(
            @PathVariable UUID zoneId,
            @Valid @RequestBody ZoneUpdateDTO request
    ) {
        return ApiResponse.<ZoneDTO>builder()
                .result(zoneService.updateZone(zoneId, request))
                .build();
    }

    //| Zone Name | Zone Type | Boundary WKT                                                                    |
    //| --------- | --------- | ------------------------------------------------------------------------------- |
    //| Zone A    | Forest    | POLYGON((105.85 21.02, 105.86 21.02, 105.86 21.03, 105.85 21.03, 105.85 21.02)) |
    //| Zone B    | Lake      | POLYGON((105.87 21.01, 105.88 21.01, 105.88 21.02, 105.87 21.02, 105.87 21.01)) |
    //| Zone C    | Urban     | POLYGON((105.83 21.00, 105.84 21.00, 105.84 21.01, 105.83 21.01, 105.83 21.00)) |
    @PostMapping("/import")
    public ApiResponse<List<ZoneDTO>> importZones(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<ZoneDTO>>builder()
                .result(zoneService.importZonesFromExcel(file))
                .build();
    }
}

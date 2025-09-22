package com.example.demo.controller.tree;

import com.example.SmartBuildingBackend.model.dto.api.ApiResponse;
import com.example.SmartBuildingBackend.model.dto.tree.GreenDTO;
import com.example.SmartBuildingBackend.service.tree.GreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/green")
@RequiredArgsConstructor
public class GreenController {

    private final GreenService greenService;

    @GetMapping("/zones/{zoneId}/gnpr")
    public ApiResponse<GreenDTO> getGnprByZone(@PathVariable Long zoneId) {
        return ApiResponse.<GreenDTO>builder()
                .result(greenService.calculateGnpr(zoneId))
                .build();
    }

    @PostMapping("/zones/gnpr")
    public ApiResponse<Map<Long, GreenDTO>> getGnprBatch(@RequestBody List<Long> zoneIds) {
        return ApiResponse.<Map<Long, GreenDTO>>builder()
                .result(greenService.calculateGnprBatch(zoneIds))
                .build();
    }

    @GetMapping("/system/gnpr")
    public ApiResponse<GreenDTO> getSystemGnpr() {
        return ApiResponse.<GreenDTO>builder()
                .result(greenService.calculateSystemGnpr())
                .build();
    }
}

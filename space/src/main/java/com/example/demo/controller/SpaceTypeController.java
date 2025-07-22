package com.example.demo.controller;

import com.example.demo.dto.request.CreatSpaceTypeRequest;
import com.example.demo.dto.request.UpdateSpaceTypeRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.SpaceTypeResponse;
import com.example.demo.service.SpaceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/space-types")
@RequiredArgsConstructor
public class SpaceTypeController {

    private final SpaceTypeService spaceTypeService;

    @PostMapping
    public ApiResponse<SpaceTypeResponse> createSpaceType(@Valid @RequestBody CreatSpaceTypeRequest request) {
        return ApiResponse.<SpaceTypeResponse>builder()
                .result(spaceTypeService.createSpaceType(request))
                .build();
    }

    @GetMapping("/{spaceTypeId}")
    public ApiResponse<SpaceTypeResponse> getSpaceType(@PathVariable Integer spaceTypeId) {
        return ApiResponse.<SpaceTypeResponse>builder()
                .result(spaceTypeService.getSpaceType(spaceTypeId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SpaceTypeResponse>> getAllSpaceTypes() {
        return ApiResponse.<List<SpaceTypeResponse>>builder()
                .result(spaceTypeService.getAllSpaceTypes())
                .build();
    }

    @PutMapping("/{spaceTypeId}")
    public ApiResponse<SpaceTypeResponse> updateSpaceType(
            @PathVariable Integer spaceTypeId,
            @Valid @RequestBody UpdateSpaceTypeRequest request
    ) {
        return ApiResponse.<SpaceTypeResponse>builder()
                .result(spaceTypeService.updateSpaceType(spaceTypeId, request))
                .build();
    }

    @DeleteMapping("/{spaceTypeId}")
    public ApiResponse<Void> deleteSpaceType(@PathVariable Integer spaceTypeId) {
        spaceTypeService.deleteSpaceType(spaceTypeId);
        return ApiResponse.<Void>builder().build();
    }
}

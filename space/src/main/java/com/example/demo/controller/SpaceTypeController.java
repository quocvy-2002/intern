package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.spacetype.SpaceTypeCreateDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeDTO;
import com.example.demo.model.dto.spacetype.SpaceTypeUpdateDTO;
import com.example.demo.service.SpaceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/space-types")
@RequiredArgsConstructor
public class SpaceTypeController {

    private final SpaceTypeService spaceTypeService;

    @PostMapping
    public ApiResponse<SpaceTypeDTO> createSpaceType(@Valid @RequestBody SpaceTypeCreateDTO request) {
        return ApiResponse.<SpaceTypeDTO>builder()
                .result(spaceTypeService.createSpaceType(request))
                .build();
    }

    @GetMapping("/{spaceTypeId}")
    public ApiResponse<SpaceTypeDTO> getSpaceType(@PathVariable Integer spaceTypeId) {
        return ApiResponse.<SpaceTypeDTO>builder()
                .result(spaceTypeService.getSpaceType(spaceTypeId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SpaceTypeDTO>> getAllSpaceTypes() {
        return ApiResponse.<List<SpaceTypeDTO>>builder()
                .result(spaceTypeService.getAllSpaceTypes())
                .build();
    }

    @PutMapping("/{spaceTypeId}")
    public ApiResponse<SpaceTypeDTO> updateSpaceType(
            @PathVariable Integer spaceTypeId,
            @Valid @RequestBody
            SpaceTypeUpdateDTO request
    ) {
        return ApiResponse.<SpaceTypeDTO>builder()
                .result(spaceTypeService.updateSpaceType(spaceTypeId, request))
                .build();
    }

    @DeleteMapping("/{spaceTypeId}")
    public ApiResponse<Void> deleteSpaceType(@PathVariable Integer spaceTypeId) {
        spaceTypeService.deleteSpaceType(spaceTypeId);
        return ApiResponse.<Void>builder().build();
    }
}

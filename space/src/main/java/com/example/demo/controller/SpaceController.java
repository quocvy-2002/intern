package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.space.SpaceCreateDTO;
import com.example.demo.model.dto.space.SpaceDTO;
import com.example.demo.model.dto.space.SpaceUpdateDTO;
import com.example.demo.service.SpaceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/spaces")
public class SpaceController {
    SpaceService spaceService;

    @PostMapping
    ApiResponse<SpaceDTO> createSpace(@Valid @RequestBody SpaceCreateDTO request) {
        return ApiResponse.<SpaceDTO>builder()
                .result(spaceService.creatSpace(request))
                .build();
    }

    @GetMapping("/{spaceId}")
    public ApiResponse<SpaceDTO> getSpace(@PathVariable Integer spaceId) {
        return ApiResponse.<SpaceDTO>builder()
                .result(spaceService.getSpcae(spaceId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SpaceDTO>> getAllSpaces() {
        return ApiResponse.<List<SpaceDTO>>builder()
                .result(spaceService.getAllSpaces())
                .build();
    }

    @DeleteMapping("/{spaceId}")
    public void deleteSpace(@PathVariable Integer spaceId) {
        spaceService.deleteSubtree(spaceId);
    }


    @PutMapping("/{spaceId}")
    public ApiResponse<SpaceDTO> updateSpace(
            @PathVariable Integer spaceId,
            @Valid @RequestBody SpaceUpdateDTO request
    ) {
        return ApiResponse.<SpaceDTO>builder()
                .result(spaceService.updateSpace(spaceId, request))
                .build();
    }
}

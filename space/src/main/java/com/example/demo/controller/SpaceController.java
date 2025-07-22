package com.example.demo.controller;

import com.example.demo.dto.request.CreatSpaceRequest;
import com.example.demo.dto.request.UpdateSpaceRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.SpaceResponse;
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
    ApiResponse<SpaceResponse> createSpace(@Valid @RequestBody CreatSpaceRequest request) {
        return ApiResponse.<SpaceResponse>builder()
                .result(spaceService.creatSpace(request))
                .build();
    }
    @GetMapping("/{spaceId}")
    public ApiResponse<SpaceResponse> getSpace(@PathVariable Integer spaceId) {
        return ApiResponse.<SpaceResponse>builder()
                .result(spaceService.getSpcae(spaceId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SpaceResponse>> getAllSpaces() {
        return ApiResponse.<List<SpaceResponse>>builder()
                .result(spaceService.getAllSpaces())
                .build();
    }

    @DeleteMapping("/{spaceId}")
    public void deleteSpace(@PathVariable Integer spaceId) {
        spaceService.deleteSubtree(spaceId);
    }


    @PutMapping("/{spaceId}")
    public ApiResponse<SpaceResponse> updateSpace(
            @PathVariable Integer spaceId,
            @Valid @RequestBody UpdateSpaceRequest request
    ) {
        return ApiResponse.<SpaceResponse>builder()
                .result(spaceService.updateSpace(spaceId, request))
                .build();
    }
}

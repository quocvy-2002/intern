package com.example.demo.controller.tree;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.tree.species.SpeciesCreateDTO;
import com.example.demo.model.dto.tree.species.SpeciesDTO;
import com.example.demo.model.dto.tree.species.SpeciesUpdateDTO;
import com.example.demo.service.tree.SpeciesService;
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
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/species")
public class SpeciesController {

    SpeciesService speciesService;

    // ========== CRUD ==========
    @PostMapping
    public ApiResponse<SpeciesDTO> create(@RequestBody SpeciesCreateDTO request) {
        return ApiResponse.<SpeciesDTO>builder()
                .result(speciesService.createSpecies(request))
                .build();
    }

    @PutMapping("/{speciesId}")
    public ApiResponse<SpeciesDTO> update(@PathVariable UUID speciesId,
                                          @RequestBody SpeciesUpdateDTO request) {
        return ApiResponse.<SpeciesDTO>builder()
                .result(speciesService.updateSpecies(speciesId, request))
                .build();
    }

    @DeleteMapping("/{speciesId}")
    public void delete(@PathVariable UUID speciesId) {
        speciesService.deleteSpecies(speciesId);
    }

    @GetMapping("/{speciesId}")
    public ApiResponse<SpeciesDTO> getById(@PathVariable UUID speciesId) {
        return ApiResponse.<SpeciesDTO>builder()
                .result(speciesService.getSpeciesById(speciesId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SpeciesDTO>> getAll() {
        return ApiResponse.<List<SpeciesDTO>>builder()
                .result(speciesService.getAllSpecies())
                .build();
    }

    @PostMapping("/batch")
    public ApiResponse<List<SpeciesDTO>> createList(@RequestBody List<SpeciesCreateDTO> requests) {
        return ApiResponse.<List<SpeciesDTO>>builder()
                .result(speciesService.createSpeciesList(requests))
                .build();
    }

    // ========== Import/Export Excel ==========

    //| **A** (0)       | **B** (1)  | **C** (2)    | **D** (3) | **E** (4) | **F** (5) |
    //| --------------- | ---------- | ------------ | --------- | --------- | --------- |
    //| Scientific Name | Local Name | Wood Density | CoeffB0   | CoeffB1   | CoeffB2   |
    @PostMapping("/import")
    public ApiResponse<List<SpeciesDTO>> importExcel(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<List<SpeciesDTO>>builder()
                .result(speciesService.importSpeciesFromExcel(file))
                .build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        ByteArrayInputStream in = speciesService.exportSpeciesToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=species.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());
    }
}
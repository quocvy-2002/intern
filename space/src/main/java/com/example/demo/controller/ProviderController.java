package com.example.demo.controller;

import com.example.demo.model.dto.provider.ProviderCreateDTO;
import com.example.demo.model.dto.provider.ProviderUpdateDTO;
import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.model.dto.provider.ProviderDTO;
import com.example.demo.service.ProviderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    ProviderService providerService;

    @PostMapping
    public ApiResponse<ProviderDTO> create(@RequestBody ProviderCreateDTO request) {
        return ApiResponse.<ProviderDTO>builder()
                .result(providerService.createProvider(request))
                .build();
    }

    @PutMapping("/{providerId}")
    public ApiResponse<ProviderDTO> update(@PathVariable Integer providerId,
                                           @RequestBody ProviderUpdateDTO request) {
        return ApiResponse.<ProviderDTO>builder()
                .result(providerService.updateProvider(providerId, request))
                .build();
    }

    @DeleteMapping("/{providerId}")
    public void delete(@PathVariable Integer providerId) {
        providerService.deleteProvider(providerId);
    }

    @GetMapping("/{providerId}")
    public ApiResponse<ProviderDTO> getById(@PathVariable Integer providerId) {
        return ApiResponse.<ProviderDTO>builder()
                .result(providerService.getProvider(providerId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProviderDTO>> getAll() {
        return ApiResponse.<List<ProviderDTO>>builder()
                .result(providerService.getProviders())
                .build();
    }
}
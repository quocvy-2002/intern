package com.example.demo.controller;

import com.example.demo.dto.request.CreateProviderRequest;
import com.example.demo.dto.request.UpdateProviderRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ProviderResponse;
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
    public ApiResponse<ProviderResponse> create(@RequestBody CreateProviderRequest request) {
        return ApiResponse.<ProviderResponse>builder()
                .result(providerService.createProvider(request))
                .build();
    }

    @PutMapping("/{providerId}")
    public ApiResponse<ProviderResponse> update(@PathVariable Integer providerId,
                                                @RequestBody UpdateProviderRequest request) {
        return ApiResponse.<ProviderResponse>builder()
                .result(providerService.updateProvider(providerId, request))
                .build();
    }

    @DeleteMapping("/{providerId}")
    public void delete(@PathVariable Integer providerId) {
        providerService.deleteProvider(providerId);
    }

    @GetMapping("/{providerId}")
    public ApiResponse<ProviderResponse> getById(@PathVariable Integer providerId) {
        return ApiResponse.<ProviderResponse>builder()
                .result(providerService.getProvider(providerId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProviderResponse>> getAll() {
        return ApiResponse.<List<ProviderResponse>>builder()
                .result(providerService.getProviders())
                .build();
    }
}
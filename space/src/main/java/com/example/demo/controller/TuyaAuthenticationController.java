package com.example.demo.controller;

import com.example.demo.model.dto.response.ApiResponse;
import com.example.demo.service.TuyaAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/tuya")
@RequiredArgsConstructor

public class TuyaAuthenticationController {
    private final TuyaAuthenticationService tuyaAuthenticationService;

    @GetMapping("/token")
    public ApiResponse<String> getToken() throws Exception {
        return ApiResponse.<String>builder()
                .result(tuyaAuthenticationService.getAccessToken())
                .build();
    }


}






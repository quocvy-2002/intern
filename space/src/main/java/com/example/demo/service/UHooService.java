package com.example.demo.service;


import com.example.demo.configuration.websocket.UHooProperties;
import com.example.demo.mapper.UHooDeviceMapper;
import com.example.demo.mapper.UHooMeasurementDataMapper;
import com.example.demo.model.dto.response.DeviceResponse;
import com.example.demo.model.dto.response.UHooMeasurementDataResponse;
import com.example.demo.model.dto.response.UHooSensorData;
import com.example.demo.model.dto.response.UhooTokenResponse;
import com.example.demo.model.entity.UHooDevice;
import com.example.demo.model.entity.UHooMeasurementData;
import com.example.demo.repository.UHooDeviceRepository;
import com.example.demo.repository.UHooMeasurementDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@Service
@RequiredArgsConstructor
public class UHooService {

    private final UHooProperties props;
    private final WebClient webClient;
    private final UHooDeviceMapper deviceMapper;
    private final UHooMeasurementDataMapper dataMapper;
    private final UHooDeviceRepository deviceRepository;
    private final UHooMeasurementDataRepository dataRepository;


    public String getToken() {
        Map<String, String> requestBody = Map.of(
                "code", props.getClientId()
        );

        UhooTokenResponse tokenResponse = webClient.post()
                .uri(props.getGenerateTokenURL())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(UhooTokenResponse.class)
                .block();

        if (tokenResponse != null) {
            props.setAccessToken(tokenResponse.getAccessToken());
            return tokenResponse.getAccessToken();
        } else {
            throw new RuntimeException("Unable to get token from UHoo API");
        }
    }

    public List<DeviceResponse> getDeviceList() {
        String token = getToken();
        System.out.println("Access token: " + token);

        List<DeviceResponse> deviceResponses = webClient.get()
                .uri(props.getDeviceList())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(DeviceResponse.class)
                .collectList()
                .block();

        return deviceResponses != null ? deviceResponses : Collections.emptyList();
    }



    public UHooMeasurementDataResponse getSensorData(String macAddress) {
        String token = getToken();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("macAddress", macAddress);
        formData.add("mode", "minute");

        // Gọi API lấy dữ liệu cảm biến
        UHooMeasurementDataResponse response = webClient.post()
                .uri(props.getDeviceData())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(UHooMeasurementDataResponse.class)
                .block();


        if (response != null && response.getData() != null && !response.getData().isEmpty()) {

            UHooSensorData latest = response.getData().get(0);

            UHooMeasurementData entity = dataMapper.toEntity(latest);

            UHooDevice device = deviceRepository.findByMacAddress(macAddress)
                    .orElseThrow(() -> new RuntimeException("Device not found: " + macAddress));
            entity.setDevice(device);

            dataRepository.save(entity);
        }

        return response;
    }

//    public UHooMeasurementDataResponse getSensorData(String macAddress) {
//        String token = getToken();
//
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("macAddress", macAddress);
//        formData.add("mode", "minute");
//
//        // Gọi API lấy dữ liệu cảm biến
//        UHooMeasurementDataResponse response = webClient.post()
//                .uri(props.getDeviceData())
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .body(BodyInserters.fromFormData(formData))
//                .retrieve()
//                .bodyToMono(UHooMeasurementDataResponse.class)
//                .block();
//
//        if (response != null && response.getData() != null && !response.getData().isEmpty()) {
//            UHooSensorData latest = response.getData().get(0);
//
//            UHooMeasurementData entity = dataMapper.toEntity(latest);
//
//            UHooDevice device = deviceRepository.findByMacAddress(macAddress)
//                    .orElseThrow(() -> new RuntimeException("Device not found: " + macAddress));
//            entity.setDevice(device);
//
//            dataRepository.save(entity);
//        }
//
//        return response;
//    }

    @Scheduled(fixedRate = 60 * 1000)
    public void autoFetchSensorData() {
        System.out.println("Starting scheduled task to fetch sensor data for all devices at " + new java.util.Date());

        List<UHooDevice> devices = deviceRepository.findAll();
        System.out.println("Found " + devices.size() + " devices in database");

        devices.forEach(device -> {
            String macAddress = device.getMacAddress();
            System.out.println("Processing device with macAddress: " + macAddress);
            UHooMeasurementDataResponse response = getSensorData(macAddress);
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                System.out.println("Successfully fetched and saved data for macAddress: " + macAddress);
            } else {
                System.out.println("No data returned for macAddress: " + macAddress);
            }
        });

        System.out.println("Completed scheduled task for fetching sensor data at " + new java.util.Date());
    }

}

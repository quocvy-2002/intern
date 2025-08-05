package com.example.demo.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceResponse {
    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("macAddress")
    private String macAddress;

    @JsonProperty("serialNumber")
    private String serialNumber;

    @JsonProperty("floorNumber")
    private int floorNumber;

    @JsonProperty("roomName")
    private String roomName;

    @JsonProperty("timezone")
    private String timezone;

    @JsonProperty("utcOffset")
    private String utcOffset;

    @JsonProperty("ssid")
    private String ssid;


}

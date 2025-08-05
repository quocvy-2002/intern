package com.example.demo.model.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooMeasurementDataResponse {
    List<UHooSensorData> data;
    Map<String, String> usersettings;
    Integer count;
}
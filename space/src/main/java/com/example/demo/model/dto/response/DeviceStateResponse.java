package com.example.demo.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceStateResponse {
    List<DeviceStatusEntry> result;
    Long t;
    Boolean success;
}
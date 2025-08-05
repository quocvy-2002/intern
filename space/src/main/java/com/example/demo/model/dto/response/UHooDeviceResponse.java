package com.example.demo.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UHooDeviceResponse {
    Long id;
    String deviceId;
    String name;
    String location;
    Date createdAt;
}
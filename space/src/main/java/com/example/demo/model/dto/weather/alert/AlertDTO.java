package com.example.demo.model.dto.weather.alert;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertDTO {
    UUID alertId;
    UUID zoneId;
    UUID treeId;
    UUID forecastId;
    String typeCode;
    String level;
    String message;
    Boolean acknowledged;
    LocalDateTime expiresAt;
    LocalDateTime createdAt;
}
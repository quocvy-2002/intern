package com.example.demo.model.dto.weather.alert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertCreateDTO {
    @NotNull(message = "IS_REQUIRED")
    UUID zoneId;

    @NotNull(message = "IS_REQUIRED")
    UUID treeId;

    @NotNull(message = "IS_REQUIRED")
    UUID forecastId;

    String typeCode;

    @NotBlank(message = "IS_REQUIRED")
    String level;

    @NotBlank(message = "IS_REQUIRED")
    String message;

    LocalDateTime expiresAt;
}
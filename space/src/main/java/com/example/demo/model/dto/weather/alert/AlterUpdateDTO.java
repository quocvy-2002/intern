package com.example.demo.model.dto.weather.alert;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlterUpdateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String level;

    @NotBlank(message = "IS_REQUIRED")
    String message;

    Boolean acknowledged;

    LocalDateTime expiresAt;
}
package com.example.demo.model.dto.weather.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationLogDTO {
    UUID logId;
    UUID alertId;
    String channel;
    String recipient;
    String message;
    LocalDateTime sentAt;
    String status;
    LocalDateTime createdAt;
}
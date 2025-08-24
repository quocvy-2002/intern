package com.example.demo.model.entity.weather;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notification_log")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationLog {

    @Id
    @Column(name = "log_id", columnDefinition = "BINARY(16)")
    UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    Alert alert;

    @Column(name = "channel", length = 50)
    String channel;

    @Column(name = "recipient", length = 255)
    String recipient;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @Column(name = "status", length = 50)
    String status;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}

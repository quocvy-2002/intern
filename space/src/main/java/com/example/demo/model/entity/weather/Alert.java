package com.example.demo.model.entity.weather;

import com.example.demo.model.entity.tree.Tree;
import com.example.demo.model.entity.tree.Zone;
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
@Table(name = "alert")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Alert {

    @Id
    @Column(name = "alert_id", columnDefinition = "BINARY(16)")
    UUID alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id")
    Tree tree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id")
    WeatherForecast forecast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code")
    AlertType type;

    @Column(name = "level", length = 50)
    String level;

    @Column(name = "message", columnDefinition = "TEXT")
    String message;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    @Column(name = "acknowledged")
    Boolean acknowledged;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}

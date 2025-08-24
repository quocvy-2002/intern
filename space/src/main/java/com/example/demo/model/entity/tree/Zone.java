package com.example.demo.model.entity.tree;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "zone", indexes = {
        @Index(name = "idx_zone_name", columnList = "zone_name")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "zone_id", columnDefinition = "BINARY(16)")
    UUID zoneId;

    @Column(name = "zone_name", nullable = false, length = 255, unique = true)
    String zoneName;

    @Column(name = "zone_type", length = 50)
    String zoneType;

    @Column(name = "boundary_wkt", columnDefinition = "TEXT")
    String boundaryWkt;

    @Builder.Default
    @Column(name = "is_active")
    Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    public void setBoundaryWkt(String wkt) {
        System.out.println("Input WKT: " + wkt); // Debug log

        if (wkt == null || wkt.trim().isEmpty()) {
            this.boundaryWkt = null;
            return;
        }

        String trimmedWkt = wkt.trim();
        System.out.println("Trimmed WKT: " + trimmedWkt); // Debug log

        if (!isValidWktFormat(trimmedWkt.toUpperCase())) {
            System.out.println("WKT validation failed"); // Debug log
            throw new IllegalArgumentException("Invalid WKT format: " + wkt);
        }

        this.boundaryWkt = trimmedWkt;
        System.out.println("Final boundaryWkt: " + this.boundaryWkt); // Debug log
    }

    private boolean isValidWktFormat(String wkt) {
        // Regex chi tiết hơn cho POLYGON
        Pattern pattern = Pattern.compile(
                "^(POLYGON|MULTIPOLYGON|POINT|MULTIPOINT|LINESTRING|MULTILINESTRING|GEOMETRYCOLLECTION)\\s*\\(.+\\)$",
                Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(wkt).matches();
    }

    @PrePersist
    @PreUpdate
    public void trimFields() {
        if (zoneName != null) {
            zoneName = zoneName.trim();
        }
        if (zoneType != null) {
            zoneType = zoneType.trim();
        }
    }
}

package com.example.demo.model.entity.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "alert_type")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertType {

    @Id
    @Column(name = "type_code", length = 50)
    String typeCode;

    @Column(name = "description", length = 255)
    String description;

    @Column(name = "threshold_value", precision = 5, scale = 2)
    BigDecimal thresholdValue;

    @Column(name = "threshold_field", length = 50)
    String thresholdField;
}

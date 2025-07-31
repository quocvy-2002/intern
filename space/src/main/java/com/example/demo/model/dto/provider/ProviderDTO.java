package com.example.demo.model.dto.provider;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "equipment_value")
public class ProviderDTO {
    Integer providerId;
    String providerName;
}

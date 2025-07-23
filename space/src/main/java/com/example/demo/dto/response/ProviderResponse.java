package com.example.demo.dto.response;

import jakarta.persistence.Entity;
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
public class ProviderResponse {
    Integer providerId;
    String providerName;
}

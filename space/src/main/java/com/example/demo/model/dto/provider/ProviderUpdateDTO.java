package com.example.demo.model.dto.provider;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProviderUpdateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String providerName;
}

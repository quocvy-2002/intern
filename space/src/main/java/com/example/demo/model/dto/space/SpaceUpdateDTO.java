package com.example.demo.model.dto.space;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceUpdateDTO {
    @NotNull(message = "IS_REQUIRED")
    Integer spaceTypeId;

    @NotNull(message = "IS_REQUIRED")
    Integer parentId;
}

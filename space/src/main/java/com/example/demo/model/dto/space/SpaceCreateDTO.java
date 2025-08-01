package com.example.demo.model.dto.space;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpaceCreateDTO {
    @NotBlank(message = "IS_REQUIRED")
    String spaceName;

    @NotNull(message = "IS_REQUIRED")
    Integer spaceTypeId;

    @NotNull(message = "IS_REQUIRED")
    Integer parentId;
}

package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatSpaceRequest {
    String spaceName;
    String spaceTypeName;
    String spaceTypeLevel;
    Integer parentId;
}

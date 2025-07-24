package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusRequest {
    String statusName;
    Integer equipmentTypeId;
    Integer equipmentId;
}

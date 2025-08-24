package com.example.demo.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QEnergyDTO {
    Integer spaceId;
    String spaceName;
    List<DailyEnergyDTO> dailyEnergyDTOS;
}

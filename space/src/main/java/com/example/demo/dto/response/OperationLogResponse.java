package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OperationLogResponse {
    private String deviceId;
    private List<OperationLogEntry> logs;

    @Data
    public static class OperationLogEntry {
        private long time;
        private String operator;
        private String action;
        private String code;
        private Object value;
    }
}

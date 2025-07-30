package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportLogResponse {
    private String deviceId;
    private List<ReportLogEntry> logs;

    @Data
    public static class ReportLogEntry {
        private long time;
        private String code;
        private Object value;
    }
}

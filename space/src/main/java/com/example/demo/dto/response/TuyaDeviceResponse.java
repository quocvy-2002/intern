package com.example.demo.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuyaDeviceResponse {
    private boolean success;
    private Result result;
    private long t;
    private String tid;

    public boolean isSuccess() { return success; }
    public Result getResult() { return result; }

    public static class Result {
        private List<Device> devices;
        private int total;
        @JsonProperty("page_no")
        private int pageNo;
        @JsonProperty("page_size")
        private int pageSize;


    }

    public static class Device {
        private String id;
        private String name;
        private List<Status> status;

        public String getId() { return id; }
        public String getName() { return name; }
        public List<Status> getStatus() { return status; }
    }

    public static class Status {
        private String code;
        private Object value;

        public String getCode() { return code; }
        public Object getValue() { return value; }
    }
}
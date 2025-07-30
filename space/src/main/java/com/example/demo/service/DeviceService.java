package com.example.demo.service;

import com.example.demo.dto.response.*;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeviceService {

    TuyaAuthenticationService tuyaAuthenticationService;
    ObjectMapper objectMapper = new ObjectMapper();

    private String makeApiRequest(String urlPath, String method, String body) {
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                String accessToken = tuyaAuthenticationService.getAccessToken();
                long t = System.currentTimeMillis();
                String nonce = UUID.randomUUID().toString().replaceAll("-", "");
                String contentSha256 = tuyaAuthenticationService.getSHA256(body != null ? body : "");
                String optionalSignatureKey = "";

                // Tách urlPath thành path và query
                String path = urlPath;
                String query = "";
                int queryIndex = urlPath.indexOf("?");
                if (queryIndex != -1) {
                    path = urlPath.substring(0, queryIndex);
                    query = urlPath.substring(queryIndex + 1);
                }

                // Sắp xếp query parameters theo thứ tự bảng chữ cái
                String sortedQuery = "";
                if (!query.isEmpty()) {
                    String[] params = query.split("&");
                    Arrays.sort(params); // Sắp xếp theo thứ tự bảng chữ cái
                    sortedQuery = String.join("&", params);
                }

                // Xây dựng stringToSign với path và query đã sắp xếp
                String stringToSign = method + "\n" + contentSha256 + "\n" + optionalSignatureKey + "\n" + path + (sortedQuery.isEmpty() ? "" : "?" + sortedQuery);

                // Tạo chuỗi để tính sign
                String str = tuyaAuthenticationService.getClientId() + accessToken + t + nonce + stringToSign;
                String sign = tuyaAuthenticationService.hmacSHA256(str, tuyaAuthenticationService.getClientSecret()).toUpperCase();

                // Log để debug
                String urlStr = tuyaAuthenticationService.getBaseUrl() + urlPath;
                System.out.println("Constructed URL: " + urlStr);
                System.out.println("String to sign: " + str);
                System.out.println("Generated sign: " + sign);
                System.out.println("Access token: " + accessToken);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod(method);
                conn.setRequestProperty("client_id", tuyaAuthenticationService.getClientId());
                conn.setRequestProperty("access_token", accessToken);
                conn.setRequestProperty("sign", sign);
                conn.setRequestProperty("t", String.valueOf(t));
                conn.setRequestProperty("sign_method", "HMAC-SHA256");
                conn.setRequestProperty("nonce", nonce);
                if (body != null) {
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    json.append(inputLine);
                }
                in.close();

                String jsonString = json.toString();
                System.out.println("Phản hồi JSON từ Tuya (" + urlPath + "): " + jsonString);

                if (responseCode == 200) {
                    return jsonString;
                } else {
                    if (jsonString.contains("token invalid") && attempt == 0) {
                        tuyaAuthenticationService.refreshAccessToken();
                        continue;
                    }
                    throw new RuntimeException("Yêu cầu API thất bại. Mã HTTP: " + responseCode + ", Phản hồi: " + jsonString);
                }

            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi gọi API Tuya: " + urlPath, e);
            }
        }
        throw new RuntimeException("Yêu cầu API thất bại sau khi thử lại: " + urlPath);
    }

    public DeviceStateResponse getDeviceFrozenState(String deviceId) {
        try {
            if (deviceId == null || deviceId.trim().isEmpty()) {
                throw new AppException(ErrorCode.DEVIC_ID_NOT_NULL);
            }

            String statusUrlPath = "/v1.0/iot-03/devices/" + deviceId + "/status";
            String statusJsonString = makeApiRequest(statusUrlPath, "GET", null);

            JSONObject statusJsonAll = new JSONObject(statusJsonString);

            if (!statusJsonAll.optBoolean("success", false)) {
                throw new RuntimeException("Lấy trạng thái thiết bị thất bại: " + statusJsonAll.optString("msg"));
            }

            JSONArray statusArray = statusJsonAll.getJSONArray("result");

            List<DeviceStatusEntry> statusList = new ArrayList<>();
            for (int j = 0; j < statusArray.length(); j++) {
                JSONObject statusObj = statusArray.getJSONObject(j);
                statusList.add(
                        DeviceStatusEntry.builder()
                                .code(statusObj.optString("code"))
                                .value(statusObj.get("value"))
                                .build()
                );
            }

            return DeviceStateResponse.builder()
                    .result(statusList)
                    .t(statusJsonAll.optLong("t"))
                    .success(true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy trạng thái thiết bị: " + e.getMessage(), e);
        }
    }




    public OperationLogResponse getOperationLogs(String deviceId, String type, long startTime, long endTime, String queryType, int size) {
        try {
            // Kiểm tra đầu vào
            if (deviceId == null || deviceId.trim().isEmpty()) {
                throw new IllegalArgumentException("Device ID không được để trống hoặc null");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Type không được để trống hoặc null");
            }
            if (startTime >= endTime) {
                throw new IllegalArgumentException("start_time phải nhỏ hơn end_time");
            }
            if (!"1".equals(queryType) && !"2".equals(queryType)) {
                throw new IllegalArgumentException("query_type phải là 1 hoặc 2");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("size phải lớn hơn 0");
            }

            // Xây dựng URL với các tham số truy vấn
            String urlPath = String.format("/v2.0/cloud/thing/%s/logs?type=%s&start_time=%d&end_time=%d&query_type=%s&size=%d",
                    deviceId, type, startTime, endTime, queryType, size);

            String jsonString = makeApiRequest(urlPath, "GET", null);
            System.out.println("URL yêu cầu API: " + tuyaAuthenticationService.getBaseUrl() + urlPath);
            JSONObject responseJson = new JSONObject(jsonString);

            if (!responseJson.optBoolean("success", false)) {
                throw new RuntimeException("Lấy nhật ký thao tác thất bại cho deviceId=" + deviceId + ": " + responseJson.optString("msg"));
            }

            JSONArray logsArray = responseJson.getJSONObject("result").optJSONArray("logs");

            OperationLogResponse dto = new OperationLogResponse();
            dto.setDeviceId(deviceId);
            List<OperationLogResponse.OperationLogEntry> entries = new ArrayList<>();

            if (logsArray != null) {
                for (int i = 0; i < logsArray.length(); i++) {
                    JSONObject log = logsArray.getJSONObject(i);
                    OperationLogResponse.OperationLogEntry entry = new OperationLogResponse.OperationLogEntry();
                    entry.setTime(log.optLong("event_time", 0));
                    entry.setOperator("unknown"); // Không có trong payload
                    entry.setAction("unknown");   // Không có trong payload
                    entry.setCode(log.optString("code"));
                    entry.setValue(log.opt("value"));
                    entries.add(entry);
                }
            }

            dto.setLogs(entries);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nhật ký thao tác cho deviceId=" + deviceId + ": " + e.getMessage(), e);
        }
    }

    public ReportLogResponse getReportingLogs(String deviceId, String codes, long startTime, long endTime) {
        try {
            String urlPath = "/v2.0/cloud/thing/" + deviceId + "/report-logs"
                    + "?codes=" + (codes != null ? codes : "all")
                    + "&start_time=" + startTime
                    + "&end_time=" + endTime;
            String jsonString = makeApiRequest(urlPath, "GET", null);
            JSONObject responseJson = new JSONObject(jsonString);

            if (!responseJson.optBoolean("success", false)) {
                throw new RuntimeException("Lấy nhật ký báo cáo thất bại: " + responseJson.optString("msg"));
            }

            JSONArray logsArray = responseJson.getJSONObject("result").optJSONArray("logs");

            ReportLogResponse dto = new ReportLogResponse();
            dto.setDeviceId(deviceId);
            List<ReportLogResponse.ReportLogEntry> entries = new ArrayList<>();

            if (logsArray != null) {
                for (int i = 0; i < logsArray.length(); i++) {
                    JSONObject log = logsArray.getJSONObject(i);
                    ReportLogResponse.ReportLogEntry entry = new ReportLogResponse.ReportLogEntry();
                    entry.setTime(log.optLong("event_time", 0));
                    entry.setCode(log.optString("code"));
                    entry.setValue(log.opt("value"));
                    entries.add(entry);
                }
            }

            dto.setLogs(entries);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy nhật ký báo cáo: " + e.getMessage(), e);
        }
    }
}
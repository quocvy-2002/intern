package com.example.demo.service;



import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.dto.response.DeviceStateResponse;
import com.example.demo.model.dto.response.DeviceStatusEntry;
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

    private String makeApiRequest(String urlPath, String method, String body) {
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                String accessToken = tuyaAuthenticationService.getAccessToken();
                long t = System.currentTimeMillis();
                String nonce = UUID.randomUUID().toString().replaceAll("-", "");
                String contentSha256 = tuyaAuthenticationService.getSHA256(body != null ? body : "");
                String optionalSignatureKey = "";

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
}
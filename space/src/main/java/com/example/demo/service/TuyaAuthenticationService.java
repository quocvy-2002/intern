package com.example.demo.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class TuyaAuthenticationService {

    @Value("${tuya.client-id}")
    private String clientId;

    @Value("${tuya.client-secret}")
    private String clientSecret;

    @Value("${tuya.base-url}")
    private String baseUrl;

    private String accessToken;
    private String refreshToken;
    private long tokenExpirationTime;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return accessToken;
        }
        return fetchNewToken();
    }

    private String fetchNewToken() {
        try {
            long t = System.currentTimeMillis();
            String nonce = UUID.randomUUID().toString().replaceAll("-", "");
            String httpMethod = "GET";
            String contentSha256 = getSHA256("");
            String urlPath = "/v1.0/token?grant_type=1";
            String stringToSign = httpMethod + "\n" + contentSha256 + "\n" + "" + "\n" + urlPath;

            String str = clientId + t + nonce + stringToSign;
            String sign = hmacSHA256(str, clientSecret).toUpperCase();

            String urlStr = baseUrl + urlPath;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("client_id", clientId);
            conn.setRequestProperty("sign", sign);
            conn.setRequestProperty("t", String.valueOf(t));
            conn.setRequestProperty("sign_method", "HMAC-SHA256");
            conn.setRequestProperty("nonce", nonce);

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
            System.out.println("Phản hồi JSON từ Tuya (Token): " + jsonString);

            if (responseCode == 200) {
                TuyaTokenResponse tokenResponse = objectMapper.readValue(jsonString, TuyaTokenResponse.class);
                if (tokenResponse.isSuccess()) {
                    this.accessToken = tokenResponse.getResult().getAccessToken();
                    this.refreshToken = tokenResponse.getResult().getRefreshToken();
                    this.tokenExpirationTime = System.currentTimeMillis() + (tokenResponse.getResult().getExpireTime() * 1000L);
                    return accessToken;
                } else {
                    throw new RuntimeException("Lấy token thất bại: " + jsonString);
                }
            } else {
                throw new RuntimeException("Lấy token thất bại. Mã HTTP: " + responseCode + ", Phản hồi: " + jsonString);
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy token Tuya: " + e.getMessage(), e);
        }
    }

    public synchronized String refreshAccessToken() {
        try {
            long t = System.currentTimeMillis();
            String nonce = UUID.randomUUID().toString().replaceAll("-", "");
            String httpMethod = "GET";
            String contentSha256 = getSHA256("");
            String urlPath = "/v1.0/token?grant_type=refresh_token&refresh_token=" + refreshToken;
            String stringToSign = httpMethod + "\n" + contentSha256 + "\n" + "" + "\n" + urlPath;

            String str = clientId + t + nonce + stringToSign;
            String sign = hmacSHA256(str, clientSecret).toUpperCase();

            String urlStr = baseUrl + urlPath;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("client_id", clientId);
            conn.setRequestProperty("sign", sign);
            conn.setRequestProperty("t", String.valueOf(t));
            conn.setRequestProperty("sign_method", "HMAC-SHA256");
            conn.setRequestProperty("nonce", nonce);

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
            System.out.println("Phản hồi JSON từ Tuya (Refresh Token): " + jsonString);

            if (responseCode == 200) {
                TuyaTokenResponse tokenResponse = objectMapper.readValue(jsonString, TuyaTokenResponse.class);
                if (tokenResponse.isSuccess()) {
                    this.accessToken = tokenResponse.getResult().getAccessToken();
                    this.refreshToken = tokenResponse.getResult().getRefreshToken();
                    this.tokenExpirationTime = System.currentTimeMillis() + (tokenResponse.getResult().getExpireTime() * 1000L);
                    return accessToken;
                } else {
                    throw new RuntimeException("Làm mới token thất bại: " + jsonString);
                }
            } else {
                throw new RuntimeException("Làm mới token thất bại. Mã HTTP: " + responseCode + ", Phản hồi: " + jsonString);
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi làm mới token Tuya: " + e.getMessage(), e);
        }
    }

    public static class TuyaTokenResponse {
        private boolean success;
        private Result result;
        private long t;
        private String tid;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public Result getResult() { return result; }
        public void setResult(Result result) { this.result = result; }

        public long getT() { return t; }
        public void setT(long t) { this.t = t; }

        public String getTid() { return tid; }
        public void setTid(String tid) { this.tid = tid; }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Result {
            @JsonProperty("access_token")
            private String accessToken;

            @JsonProperty("refresh_token")
            private String refreshToken;

            @JsonProperty("expire_time")
            private int expireTime;

            public String getAccessToken() { return accessToken; }
            public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

            public String getRefreshToken() { return refreshToken; }
            public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

            public int getExpireTime() { return expireTime; }
            public void setExpireTime(int expireTime) { this.expireTime = expireTime; }
        }
    }

    public String hmacSHA256(String data, String key) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getSHA256(String input) throws Exception {
        if (input.isEmpty()) {
            return "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

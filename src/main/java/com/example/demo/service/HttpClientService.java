package com.example.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpClientService {

    private final OkHttpClient okHttpClient;

    @Value("${http.endpoint.url}")
    private String endpointUrl;

    @Value("${http.endpoint.bearer-token}")
    private String bearerToken; //bearer token

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson mapper
    public List<Map<String, Object>> callExternalService(String payload) {
        Request request = new Request.Builder()
                .url(endpointUrl)
                .get()
                .addHeader("Authorization", "Bearer " + bearerToken) // if needed
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP call failed: " + response.code());
            }
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, new TypeReference<List<Map<String,Object>>>(){});
        } catch(IOException e) {
            throw new RuntimeException("Error calling external service", e);
        }
    }

}


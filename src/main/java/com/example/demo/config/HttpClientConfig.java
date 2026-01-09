package com.example.demo.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration

public class HttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5)) //Sets the maximum time to establish a TCP connection to the remote server
                .readTimeout(Duration.ofSeconds(10)) //Sets the maximum time to wait for the server to send a response after connection is established
                .build();
    }
}

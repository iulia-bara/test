package com.example.demo.service;

import com.example.demo.model.InputMessage;
import com.example.demo.model.OutputMessage;
import com.example.demo.producer.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class MessageProcessor {

    private final KafkaProducerService producer;
    private final HttpClientService httpClientService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //get ENDPOINT

    public void process(InputMessage message) {

        List<Map<String, Object>> httpResponse = httpClientService.callExternalService(null);

        OutputMessage output = new OutputMessage(message.id(), Map.of("data",httpResponse));

        producer.send(output);

        try {
            System.out.println(
                    objectMapper
                            .writerWithDefaultPrettyPrinter()
                            .writeValueAsString(output)
            );
        } catch (Exception ignored) {}
    }
}

//post endpoint
//    public void process(InputMessage message) {
//        try {
//            // Convert payload Map to JSON string for HTTP request
//            String payloadJson = objectMapper.writeValueAsString(message.payload());
//
//            // Call HTTP service → returns Map<String,Object> representing HTTP response
//            Map<String, Object> httpResponse = httpClientService.callExternalService(payloadJson);
//
//            // Extract only the "json" part of the HTTP response
//            Map<String, Object> responseJson = (Map<String, Object>) httpResponse.get("json");
//
//            // Wrap the extracted JSON into OutputMessage
//            OutputMessage output = new OutputMessage(message.id(), responseJson);
//
//            // Send to Kafka
//            producer.send(output);
//
//            // Pretty-print JSON in logs
//            String prettyOutput = objectMapper.writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(output);
//            System.out.println("Produced message to output-topic:\n" + prettyOutput);
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Failed to process payload or output message", e);
//        }
//    }




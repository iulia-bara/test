package com.example.demo.consumer;

import com.example.demo.model.InputMessage;
import com.example.demo.service.MessageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor

public class KafkaConsumerService {
    private final MessageProcessor processor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    //This is the business logic layer.
    //The consumer itself is thin: it just receives a message and delegates processing.

    @KafkaListener(topics = "${kafka.topic.input}", groupId = "connector-group")
        //This tells Spring Kafka that this method should listen to messages on a Kafka topic.
        //topics = "${kafka.topic.input}" → reads the topic name from application.yml.
        //groupId = "connector-group" → Kafka consumer group ID (ensures load balancing and offset tracking).

    public void consume(InputMessage message) {
        System.out.println("Consumed message: " + message);
        processor.process(message);  // pass the map payload
    }
            //The consumer itself does NOT contain business logic.
            //It passes the message to the MessageProcessor:
                //Transform the payload
                //Call external HTTP services
                //the output message to Kafka
        //This method is automatically invoked every time a message arrives on the input topic.
        //Spring Kafka deserializes the Kafka message into InputMessage
}

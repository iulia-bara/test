package com.example.demo.producer;

import com.example.demo.model.OutputMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, OutputMessage> kafkaTemplate;

    @Value("${kafka.topic.output}") //Reads the output topic name from application.yml
    //This makes producer configurable without hardcoding topic names
    private String outputTopic;

    public void send(OutputMessage message) {
        kafkaTemplate.send(outputTopic, message);
        System.out.println("Produced to output-topic: " + message);
    }
    //Sends the message payload to the Kafka output topic.

}

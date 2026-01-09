package com.example.demo.config;

import com.example.demo.model.OutputMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, OutputMessage> producerFactory() //responsible for creating Kafka producers
    {
        Map<String, Object> config = new HashMap<>(); //Kafka producers need a configuration map to know how to connect and serialize messages
        config.put("bootstrap.servers", "localhost:9092"); //tells the producer where the Kafka brokers are
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //Kafka messages have keys and value; The KEY is serialized before sending over the network
        //StringSerializer converts Java(String) into bytes

        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //Same as key serializer, but for the message value
        //can be replaced with other serializers

        return new DefaultKafkaProducerFactory<>(config);
        //This factory uses the configuration map to create producers
        //Spring Kafka uses this factory internally whenever send messages
    }

    @Bean
    public KafkaTemplate<String, OutputMessage> kafkaTemplate() { //KafkaTemplate is Spring Kafka’s main API for sending messages
        return new KafkaTemplate<>(producerFactory());
    }
    // inject KafkaTemplate in producer service and just call
    //Spring uses the ProducerFactory to create a producer under the hood
}

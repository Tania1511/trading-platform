package com.tradingPlatform.orderGateway.config;

import com.tradingPlatform.orderGateway.event.OrderPlacedEvent;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


    @Bean
    public ProducerFactory<String, OrderPlacedEvent> producerFactory(KafkaProperties kafkaProperties){
        Map<String,Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties()); // get properties defined in application.yml
//        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"kafka:9092");
//        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate(ProducerFactory<String, OrderPlacedEvent> producerFactory){
       return new KafkaTemplate<>(producerFactory);
    }
}

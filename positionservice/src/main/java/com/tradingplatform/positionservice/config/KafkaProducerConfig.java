package com.tradingplatform.positionservice.config;

import com.tradingplatform.positionservice.event.PositionUpdatedEvent;
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
    public ProducerFactory<String,PositionUpdatedEvent> producerFactory(KafkaProperties kafkaProperties){
        Map<String,Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, PositionUpdatedEvent> kafkaTemplate(ProducerFactory<String,PositionUpdatedEvent> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}

package com.tradingPlatform.orderGateway.config;

import com.tradingPlatform.orderGateway.event.OrderCanceledEvent;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OderCanceledEventKafkaConfig {


    @Bean
    public ProducerFactory<String, OrderCanceledEvent> orderCanceledEventProducerFactory(KafkaProperties kafkaProperties){
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderCanceledEvent> orderCanceledEventKafkaTemplate(ProducerFactory<String, OrderCanceledEvent> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}

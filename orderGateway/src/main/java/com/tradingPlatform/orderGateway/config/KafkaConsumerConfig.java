package com.tradingPlatform.orderGateway.config;

import com.tradingPlatform.orderGateway.event.OrderPlacedEvent;
import com.tradingPlatform.orderGateway.event.PositionUpdatedEvent;
import com.tradingPlatform.orderGateway.event.TradeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> orderPlacedEventConsumerFactory(KafkaProperties kafkaProperties){
        Map<String,Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(OrderPlacedEvent.class,false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> orderPlacedEventListenerContainerFactory(
            ConsumerFactory<String, OrderPlacedEvent> orderPlacedEventConsumerFactory){
        ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderPlacedEventConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TradeEvent> tradeEventConsumerFactory(KafkaProperties kafkaProperties){
        Map<String,Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(TradeEvent.class,false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TradeEvent> tradeEventListenerContainerFactory(
            ConsumerFactory<String, TradeEvent> tradeEventConsumerFactory){
        ConcurrentKafkaListenerContainerFactory<String, TradeEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(tradeEventConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PositionUpdatedEvent> positionUpdatedEventConsumerFactory(KafkaProperties kafkaProperties){
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(PositionUpdatedEvent.class,false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PositionUpdatedEvent> positionUpdatedEventListenerContainerFactory(
            ConsumerFactory<String, PositionUpdatedEvent> positionUpdatedEventConsumerFactory){
        ConcurrentKafkaListenerContainerFactory<String, PositionUpdatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(positionUpdatedEventConsumerFactory);
        return factory;
    }

}

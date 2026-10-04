package com.tradingPlatform.orderGateway.config;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import tools.jackson.databind.ser.jdk.StringSerializer;
import com.tradingplatform.schemas.OrderCanceledEvent;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OderCanceledEventKafkaConfig {


    @Bean
    public ProducerFactory<String, OrderCanceledEvent> orderCanceledEventProducerFactory(KafkaProperties kafkaProperties){
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class);
        configProps.put(SerdeConfig.REGISTRY_URL, "http://apicurio-registry:8080/apis/registry/v2");
        configProps.put(SerdeConfig.AUTO_REGISTER_ARTIFACT, "true");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, OrderCanceledEvent> orderCanceledEventKafkaTemplate(ProducerFactory<String, OrderCanceledEvent> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}

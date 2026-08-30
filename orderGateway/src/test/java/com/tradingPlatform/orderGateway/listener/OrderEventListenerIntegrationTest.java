package com.tradingPlatform.orderGateway.listener;

import com.tradingPlatform.orderGateway.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
//@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1,topics = "order-event")
@TestPropertySource(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class OrderEventListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate ;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @Test
    void publishingEvent_triggerListenerConsumption(){
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(),UUID.randomUUID(),"c11","AAPL","BUY",new BigDecimal("150.25"),new BigDecimal("10"), Instant.now());

        kafkaTemplate.send("order-event",event.symbol(),event);

        ArgumentCaptor<OrderPlacedEvent> captor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(orderEventListener, timeout(5000)).onOrderPlaced(captor.capture());

        OrderPlacedEvent received = captor.getValue();
        assertThat(received.eventId()).isEqualTo(event.eventId());
        assertThat(received.symbol()).isEqualTo("AAPL");
        assertThat(received.price()).isEqualTo(new BigDecimal("150.25"));
        assertThat(received.quantity()).isEqualTo(new BigDecimal("10"));
    }
}

package com.tradingPlatform.orderGateway.listener;

import com.tradingPlatform.orderGateway.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.tradingplatform.schemas.OrderPlacedEvent;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private static final String TOPIC = "order-events";
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventListener(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //    @KafkaListener(topics = "order-events", groupId = "order-gateway-local-consumer")
//    public void onOrderPlaced(OrderPlacedEvent event){
//        log.info("Consume OrderPlacedEvent: eventId={}, orderID={}, symbol={}, side={}, price={},quantity={}",
//                event.eventId(),event.orderId(), event.symbol(), event.side(),event.price(), event.quantity());
//    }


    public void publishOrderPlaced(Order order){
        OrderPlacedEvent event = OrderPlacedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOrderId(order.getId().toString())
                .setClientOrderId(order.getClientOrderId())
                .setSymbol(order.getSymbol())
                .setSide(order.getSide().name())
                .setPrice(order.getPrice().toString())
                .setQuantity(order.getQuantity().toString())
                .setOccurredAt(Instant.now().toString())
                .build();

        String key = order.getSymbol();

        kafkaTemplate.send(TOPIC, key, event).whenComplete((result,ex) -> {
            if(ex !=null) {
                log.error("Failed to publish OrderPlacedEvent for clientOrderID={}", order.getClientOrderId(), ex);
            }else {
                log.info("Published OrderPlacedEvent for clientOrderId={} to partition={}, offset={}",
                    order.getClientOrderId(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });

    }

}

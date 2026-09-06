package com.tradingPlatform.orderGateway.event;

import com.tradingPlatform.orderGateway.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class OrderCanceledEventPublisher {

    private static Logger log = LoggerFactory.getLogger(OrderCanceledEventPublisher.class);

    private static final String TOPIC = "order-cancelled-events";

    private final KafkaTemplate<String, OrderCanceledEvent> kafkaTemplate;


    public OrderCanceledEventPublisher(KafkaTemplate<String, OrderCanceledEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCanceled(Order order){
        OrderCanceledEvent event = new OrderCanceledEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getClientOrderId(),
                order.getSymbol(),
                order.getSide().name(),
                order.getPrice(),
                order.getQuantity(),
                order.getFilledQuantity(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );

        kafkaTemplate.send(TOPIC,order.getClientOrderId(),event).whenComplete((result,ex) -> {
           if(ex!=null){
               log.error("Failed to publish OrderCanceledEvent for clientId={}",order.getClientOrderId(),ex);
           }
        });
    }
}

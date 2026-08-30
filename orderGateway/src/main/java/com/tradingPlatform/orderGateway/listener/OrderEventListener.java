package com.tradingPlatform.orderGateway.listener;

import com.tradingPlatform.orderGateway.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);


//    @KafkaListener(topics = "order-event", groupId = "order-gateway-local-consumer")
    public void onOrderPlaced(OrderPlacedEvent event){
        log.info("Consume OrderPlacedEvent: eventId={}, orderID={}, symbol={}, side={}, price={},quantity={}",
                event.eventId(),event.orderId(), event.symbol(), event.side(),event.price(), event.quantity());
    }
}

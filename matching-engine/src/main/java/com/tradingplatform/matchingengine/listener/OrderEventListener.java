package com.tradingplatform.matchingengine.listener;


import com.tradingplatform.matchingengine.book.OrderBookManager;
import com.tradingplatform.matchingengine.event.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final OrderBookManager orderBookManager;

    public OrderEventListener(OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    @KafkaListener(topics = "order-event",groupId = "matching-engine")
    public void onOrderPlaced(OrderPlacedEvent event){
        orderBookManager.handle(event);
    }

}

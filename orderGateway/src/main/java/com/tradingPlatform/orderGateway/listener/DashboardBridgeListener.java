package com.tradingPlatform.orderGateway.listener;

import com.tradingPlatform.orderGateway.event.OrderPlacedEvent;
import com.tradingPlatform.orderGateway.event.PositionUpdatedEvent;
import com.tradingPlatform.orderGateway.event.TradeEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class DashboardBridgeListener {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public DashboardBridgeListener(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "dashboard-bridge", containerFactory = "orderPlacedEventListenerContainerFactory")
    public void onOrderPlaced(OrderPlacedEvent event){
        simpMessagingTemplate.convertAndSend("/topic/orders",event);
    }

    @KafkaListener(topics = "trade-events", groupId = "dashboard-bridge", containerFactory = "tradeEventListenerContainerFactory")
    public void onTrade(TradeEvent event){
        simpMessagingTemplate.convertAndSend("/topic/trades", event);
    }

    @KafkaListener(topics = "position-events", groupId = "dashboard-bridge", containerFactory = "positionUpdatedEventListenerContainerFactory")
    public void onPositionUpdated(PositionUpdatedEvent event){
        simpMessagingTemplate.convertAndSend("/topic/positions", event);
    }
}

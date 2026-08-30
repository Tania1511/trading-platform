package com.tradingplatform.positionservice.listener;

import com.tradingplatform.positionservice.event.TradeEvent;
import com.tradingplatform.positionservice.service.PositionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeEventListener {

    private final PositionService positionService;

    public TradeEventListener(PositionService positionService) {
        this.positionService = positionService;
    }

    @KafkaListener(topics = "trade-events", groupId = "position-service")
    public void onTrade(TradeEvent event){
        positionService.applyTrade(event);
    }

}

package com.tradingplatform.matchingengine.event;

import com.tradingplatform.matchingengine.book.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class TradeEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TradeEventPublisher.class);

    private static  final  String TOPIC="trade-events";

    private KafkaTemplate<String,TradeEvent> kafkaTemplate ;

    public TradeEventPublisher(KafkaTemplate<String,TradeEvent> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }

    public void publishEvent(Trade trade){
        TradeEvent event = TradeEvent.from(trade);

        String key = event.symbol();

        kafkaTemplate.send(TOPIC,key,event).whenComplete((result,ex) -> {
           if(ex!=null){
               log.error("Failed to publish TradeEvent for tradeId={}",trade.tradeId(), ex);
           }
           else{
               log.info("Published TradeEvent tradeId={}, symbol={}, price={}, quantity={} to partition={}, offset={}",
                       trade.tradeId(),trade.symbol(),trade.price(),trade.quantity(),result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
           }
        });
    }
}

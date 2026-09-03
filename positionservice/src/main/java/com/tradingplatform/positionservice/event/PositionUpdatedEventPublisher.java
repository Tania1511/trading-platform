package com.tradingplatform.positionservice.event;

import com.tradingplatform.positionservice.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PositionUpdatedEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PositionUpdatedEventPublisher.class);

    private final KafkaTemplate<String,PositionUpdatedEvent> kafkaTemplate;

    public PositionUpdatedEventPublisher(KafkaTemplate<String, PositionUpdatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String TOPIC = "position-event";

    public void publish(Position position){
        PositionUpdatedEvent event = new PositionUpdatedEvent(
                position.getAccountKey(),
                position.getSymbol(),
                position.getAverageCost(),
                position.getQuantity(),
                position.getRealizedPnl(),
                position.getUpdatedAt()
        );

        kafkaTemplate.send(TOPIC,position.getAccountKey(),event).whenComplete((result,ex) ->{
            if(ex!=null){
                log.error("Failed to publish PositionUpdatedEvent fpr account key={} symbol={}",position.getAccountKey(),position.getSymbol());
            }
        });
    }
}

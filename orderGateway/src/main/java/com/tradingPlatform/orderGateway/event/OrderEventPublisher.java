package com.tradingPlatform.orderGateway.event;

import com.tradingPlatform.orderGateway.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String ,OrderPlacedEvent> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String ,OrderPlacedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final  String TOPIC = "order-event";


    public void publishOrderPlaced(Order order){

        OrderPlacedEvent  event = OrderPlacedEvent.from(order);
        String key = order.getSymbol();

        log.info("Publishing orderPlaced event for clientId={} to topic = {} with key={}",order.getClientOrderId(),TOPIC,key);

        kafkaTemplate.send(TOPIC,key,event).whenComplete((result,ex) ->{
            if(ex!=null){
                log.error("Failed to publish order placed event for clientOrderId={}",order.getClientOrderId(),ex);
            }else{
                log.info("published order placed event for clientOrderId={} to partition={}, offset={}",
                        order.getClientOrderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

}

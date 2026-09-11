package com.tradingplatform.surveillance_service.listener;

import com.tradingplatform.surveillance_service.agent.SurveillanceAgentService;
import com.tradingplatform.surveillance_service.detector.SpoofingDetector;
import com.tradingplatform.surveillance_service.detector.SpoofingSignal;
import com.tradingplatform.surveillance_service.event.OrderCanceledEvent;
import com.tradingplatform.surveillance_service.model.SurveillanceAlert;
import com.tradingplatform.surveillance_service.repository.SurveillanceAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderCanceledEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCanceledEventListener.class);

    private final SpoofingDetector spoofingDetector;
    private final SurveillanceAgentService surveillanceAgentService;
    private final SurveillanceAlertRepository surveillanceAlertRepository;


    public OrderCanceledEventListener(SpoofingDetector spoofingDetector, SurveillanceAgentService surveillanceAgentService, SurveillanceAlertRepository surveillanceAlertRepository) {
        this.spoofingDetector = spoofingDetector;
        this.surveillanceAgentService = surveillanceAgentService;
        this.surveillanceAlertRepository = surveillanceAlertRepository;
    }


    @KafkaListener(topics = "order-cancelled-events", groupId = "surveillance-service")
    public void onOrderCancel(OrderCanceledEvent event){

        Optional<SpoofingSignal> signal = spoofingDetector.evaluate(event);

        if(signal.isEmpty()){
            return;
        }

        log.info("Spoofing rule fired for clientOrderId={} symbol={} - invoking agent investigation",
                event.clientOrderId(), event.symbol());

        SurveillanceAlert alert = new SurveillanceAlert(
                event.orderId(), event.clientOrderId(), event.symbol(),
                event.originalQuantity(), event.filledQuantity(),
                signal.get().fillRatio(), signal.get().secondsToCancel()
        );


        String explanation = surveillanceAgentService.investigate(event, signal.get());
        alert.setAiExplanation(explanation);

        surveillanceAlertRepository.save(alert);

        log.info("Surveillance alert saved for clientOrderId={}: {}",
                event.clientOrderId(), explanation);
    }
}

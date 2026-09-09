package com.tradingplatform.surveillance_service.detector;

import com.tradingplatform.surveillance_service.event.OrderCanceledEvent;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

@Component
public class SpoofingDetector {

    private final SpoofingDetectionProperties properties;


    public SpoofingDetector(SpoofingDetectionProperties properties) {
        this.properties = properties;
    }

    public Optional<SpoofingSignal> evaluate(OrderCanceledEvent event){
        if(event.originalQuantity().compareTo(properties.getLargeQuantityThreshold()) < 0 ){
            return Optional.empty();
        }

        BigDecimal fillRatio = event.originalQuantity().signum() == 0
                ? BigDecimal.ZERO
                : event.filledQuantity().divide(event.originalQuantity(), 4, RoundingMode.HALF_UP);

        if(fillRatio.compareTo(properties.getMaxfillRatio()) > 0)
            return Optional.empty();

        long secondsToCancel = Duration.between(event.placedAt(), event.cancelledAt()).getSeconds();

        if(secondsToCancel > properties.getMaxSecondsToCancel())
            return Optional.empty();

        return Optional.of(new SpoofingSignal(fillRatio,secondsToCancel));
    }
}

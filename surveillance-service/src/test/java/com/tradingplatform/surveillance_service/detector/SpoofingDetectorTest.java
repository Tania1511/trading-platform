package com.tradingplatform.surveillance_service.detector;

import com.tradingplatform.surveillance_service.event.OrderCanceledEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SpoofingDetectorTest {

    private final SpoofingDetector spoofingDetector = new SpoofingDetector(new SpoofingDetectionProperties());

    private OrderCanceledEvent event(BigDecimal originalQty, BigDecimal filledQty, long secondsOpen) {
        Instant placedAt = Instant.now().minus(secondsOpen, ChronoUnit.SECONDS);
        return new OrderCanceledEvent(UUID.randomUUID(), UUID.randomUUID(), "client-1", "AAPL", "BUY",
                new BigDecimal("150.00"), originalQty, filledQty, placedAt, Instant.now());
    }


    @Test
    void largeOrder_mostlyUnfilled_canceledQuickly_isFlagged(){
        Optional<SpoofingSignal> signal = spoofingDetector.evaluate(event(new BigDecimal("100"), new BigDecimal("2"),5));

        assertThat(signal).isPresent();
        assertThat(signal.get().fillRatio()).isEqualByComparingTo("0.0200");
        assertThat(signal.get().secondsToCancel()).isEqualTo(5);
    }


    @Test
    void smallOrder_isNeverFlagged_regardlessOfFillRatioOrSpeed() {
        Optional<SpoofingSignal> signal = spoofingDetector.evaluate(event(new BigDecimal("100"), new BigDecimal("2"),5));

    }


}

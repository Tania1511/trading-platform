package com.tradingplatform.surveillance_service.detector;

import java.math.BigDecimal;

public record SpoofingSignal(
        BigDecimal fillRatio,
        long secondsToCancel
) {
}

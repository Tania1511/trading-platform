package com.tradingplatform.surveillance_service.dto;

import com.tradingplatform.surveillance_service.model.SurveillanceAlert;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        UUID orderId,
        String clientOrderId,
        String symbol,
        BigDecimal originalQuantity,
        BigDecimal filledQuantity,
        BigDecimal fillRatio,
        long secondsToCancel,
        String aiExplanation,
        Instant createdAt
) {

    public static AlertResponse from(SurveillanceAlert alert) {
        return new AlertResponse(
                alert.getId(), alert.getOrderId(), alert.getClientOrderId(), alert.getSymbol(),
                alert.getOriginalQuantity(), alert.getFilledQuantity(), alert.getFillRatio(),
                alert.getSecondsToCancel(), alert.getAiExplanation(), alert.getCreatedAt()
        );
    }
}

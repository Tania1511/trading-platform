package com.tradingplatform.surveillance_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCanceledEvent(
        UUID eventId,
        UUID orderId,
        String clientOrderId,
        String symbol,
        String side,
        BigDecimal price,
        BigDecimal originalQuantity,
        BigDecimal filledQuantity,
        Instant placedAt,
        Instant cancelledAt
){
}

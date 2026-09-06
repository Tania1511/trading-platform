package com.tradingPlatform.orderGateway.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCanceledEvent(
        UUID eventId,
        UUID tradeId,
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

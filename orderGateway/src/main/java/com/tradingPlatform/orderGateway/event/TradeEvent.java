package com.tradingPlatform.orderGateway.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeEvent(
        UUID eventId,
        UUID tradeId,
        String symbol,
        UUID buyOrderId,
        UUID sellOrderId,
        String buyClientOrderId,
        String sellClientOrderId,
        BigDecimal price,
        BigDecimal quantity,
        Instant occurredAt
) {
}

package com.tradingplatform.matchingengine.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderPlacedEvent(
        UUID eventId,
        UUID orderId,
        String clientOrderId,
        String symbol,
        String side,
        BigDecimal price,
        BigDecimal quantity,
        Instant occurredAt
) {


}

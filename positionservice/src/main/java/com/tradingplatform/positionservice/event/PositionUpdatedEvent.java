package com.tradingplatform.positionservice.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PositionUpdatedEvent(
        String accountKey,
        String symbol,
        BigDecimal price,
        BigDecimal quantity,
        BigDecimal realizedPnL,
        Instant updatedAt
) {

}

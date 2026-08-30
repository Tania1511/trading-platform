package com.tradingplatform.matchingengine.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Trade(

    UUID tradeId,
    String symbol,
    UUID buyOderId,
    UUID sellOrderId,
    String buyClientId,
    String sellClientId,
    BigDecimal quantity,
    BigDecimal price,
    Instant occurredAt
){}


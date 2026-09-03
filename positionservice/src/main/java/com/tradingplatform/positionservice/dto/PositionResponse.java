package com.tradingplatform.positionservice.dto;

import com.tradingplatform.positionservice.model.Position;

import java.math.BigDecimal;
import java.time.Instant;

public record PositionResponse (
    String accountKey,
    String symbol,
    BigDecimal averageCost,
    BigDecimal quantity,
    BigDecimal realizedPnL,
    Instant updatedAt
){
    public static PositionResponse from(Position position){
        return new PositionResponse(
                position.getAccountKey(),
                position.getSymbol(),
                position.getAverageCost(),
                position.getQuantity(),
                position.getRealizedPnl(),
                position.getUpdatedAt()
        );
    }

}

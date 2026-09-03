package com.tradingplatform.positionservice.dto;

import com.tradingplatform.positionservice.model.Trade;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse (
        UUID tradeId,
        String symbol,
        String buyClientOrderId,
        String sellClientOrderId,
        BigDecimal quantity,
        BigDecimal price,
        Instant occurredAt
){

    public static TradeResponse from(Trade trade){
        return new TradeResponse(
                trade.getTradeId(),
                trade.getSymbol(),
                trade.getBuyClientOrderId(),
                trade.getSellClientOrderId(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getOccurredAt()
        );
    }
}

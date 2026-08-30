package com.tradingplatform.matchingengine.event;

import com.tradingplatform.matchingengine.book.Trade;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeEvent (
        UUID eventId,
        UUID tradeId,
        String symbol,
        UUID buyOrderId,
        UUID sellOrderID,
        String buyClientOrderId,
        String sellClientOrderId,
        BigDecimal price,
        BigDecimal quantity,
        Instant occurredAt

){

    public static TradeEvent from(Trade trade){
        return new TradeEvent(
                UUID.randomUUID(),
                trade.tradeId(),
                trade.symbol(),
                trade.buyOderId(),
                trade.sellOrderId(),
                trade.buyClientId(),
                trade.sellClientId(),
                trade.price(),
                trade.quantity(),
                trade.occurredAt()
        );
    }

}

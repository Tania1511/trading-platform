package com.tradingPlatform.orderGateway.event;

import com.tradingPlatform.orderGateway.model.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderPlacedEvent (

    UUID eventId,
    UUID orderId,
    String clientOrderId,
    String symbol,
    String side,
    BigDecimal price,
    BigDecimal quantity,
    Instant occurredAt
){

    public static OrderPlacedEvent from(Order order){
        return new OrderPlacedEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getClientOrderId(),
                order.getSymbol(),
                order.getSide().name(),
                order.getPrice(),
                order.getQuantity(),
                Instant.now()
        );
    };
}

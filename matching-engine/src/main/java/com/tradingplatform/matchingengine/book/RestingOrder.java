package com.tradingplatform.matchingengine.book;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RestingOrder {

    private final UUID orderId;
    private final String clientOrderId;
    private final String symbol;
    private final Side side;
    private BigDecimal remainingQuantity;
    private final BigDecimal price;
    private final Instant placedAt;

    private final long sequence;


    public enum Side {BUY,SELL}

    public RestingOrder (UUID orderId, String clientOrderId, String symbol, Side side, BigDecimal quantity, BigDecimal price, Instant placedAt, long sequence){
        this.orderId=orderId;
        this.clientOrderId=clientOrderId;
        this.symbol=symbol;
        this.side=side;
        this.remainingQuantity=quantity;
        this.price=price;
        this.placedAt=placedAt;
        this.sequence=sequence;
    }

    public boolean isFullyFilled(){
        return remainingQuantity.compareTo(BigDecimal.ZERO)==0;
    }

    public void reduceRemainingQuantityBy(BigDecimal filledQty){
        this.remainingQuantity = remainingQuantity.subtract(filledQty);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getPlacedAt() {
        return placedAt;
    }

    public long getSequence() {
        return sequence;
    }
}

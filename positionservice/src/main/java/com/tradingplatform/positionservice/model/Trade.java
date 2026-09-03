package com.tradingplatform.positionservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="trade_id", nullable = false)
    private UUID tradeId;

    @Column(nullable = false)
    private String symbol;

    @Column(name="buy_client_order_id", nullable = false)
    private String buyClientOrderId;

    @Column(name = "sell_client_order_id", nullable = false)
    private String sellClientOrderId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    public Trade(UUID tradeId, String symbol, String buyClientOrderId, String sellClientOrderId, BigDecimal price, BigDecimal quantity, Instant occurredAt){
        this.tradeId = tradeId;
        this.symbol = symbol;
        this.buyClientOrderId = buyClientOrderId;
        this.sellClientOrderId = sellClientOrderId;
        this.price = price;
        this.quantity = quantity;
        this.occurredAt = occurredAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getBuyClientOrderId() {
        return buyClientOrderId;
    }

    public String getSellClientOrderId() {
        return sellClientOrderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}

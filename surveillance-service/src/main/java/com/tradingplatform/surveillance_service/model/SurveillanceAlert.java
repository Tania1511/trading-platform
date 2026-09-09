package com.tradingplatform.surveillance_service.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "surveillance_alerts")
public class SurveillanceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID orderId;

    private String clientOrderId;

    private String symbol;

    private BigDecimal originalQuantity;

    private BigDecimal filledQuantity;

    private BigDecimal fillRatio;

    private long secondsToCancel;

    private String aiExplanation;

    private Instant createdAt;


    protected SurveillanceAlert(){};

    public SurveillanceAlert(UUID orderId, String clientOrderId, String symbol, BigDecimal originalQuantity,
                             BigDecimal filledQuantity, BigDecimal fillRatio, long secondsToCancel){
        this.orderId = orderId;
        this.clientOrderId = clientOrderId;
        this.symbol = symbol;
        this.originalQuantity = originalQuantity;
        this.filledQuantity = filledQuantity;
        this.fillRatio = fillRatio;
        this.secondsToCancel = secondsToCancel;
        this.createdAt = Instant.now();
    }

    public void setAiExplanation(String aiExplanation){
        this.aiExplanation = aiExplanation;
    }

    public UUID getId() {
        return id;
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

    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public BigDecimal getFillRatio() {
        return fillRatio;
    }

    public long getSecondsToCancel() {
        return secondsToCancel;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

package com.tradingPlatform.orderGateway.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //Idempotency Key
    @Column(name="client_order_id" ,nullable = false, unique = true)
    private String clientOrderId;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide side; //BUY or SELL


    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name="filled_quantity",nullable = false, precision = 19, scale = 4)
    private BigDecimal filledQuantity = BigDecimal.ZERO;   //how much has traded so far

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @Column(name = "created_at", nullable=false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected Order(){}

    public Order(String clientOrderId, String symbol, OrderSide side, BigDecimal price, BigDecimal quantity){
        this.clientOrderId = clientOrderId;
        this.symbol=symbol;
        this.side=side;
        this.price=price;
        this.quantity=quantity;
        this.filledQuantity = BigDecimal.ZERO;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getFilledQuantity() {
        return filledQuantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void cancel(){
        if(this.status==OrderStatus.FILLED || this.status==OrderStatus.CANCELED)
            throw new IllegalStateException("Cannot cancel an order in terminal state : " + this.status);
        this.status=OrderStatus.CANCELED;
        this.updatedAt=Instant.now();
    }

    public void reject(){
        if(this.status!=OrderStatus.NEW)
            throw new IllegalStateException("Only a new order can be rejected, current state : " + this.status);
        this.status=OrderStatus.REJECTED;
        this.updatedAt=Instant.now();
    }
}

package com.tradingPlatform.orderGateway.dto;

import com.tradingPlatform.orderGateway.model.Order;
import com.tradingPlatform.orderGateway.model.OrderSide;
import com.tradingPlatform.orderGateway.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class OrderResponse {

    private UUID id;
    private String clientOrderId;
    private String symbol;
    private OrderSide side;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal filledQuantity;
    private OrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;


    public static OrderResponse from(Order order){
        return new OrderResponse(order.getId(),order.getClientOrderId(),order.getSymbol(),order.getSide(),order.getPrice(),
                order.getQuantity(),order.getFilledQuantity(),order.getStatus(),order.getCreatedAt(),order.getUpdatedAt());
    }
}

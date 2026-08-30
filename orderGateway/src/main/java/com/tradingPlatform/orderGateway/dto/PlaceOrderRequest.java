package com.tradingPlatform.orderGateway.dto;

import com.tradingPlatform.orderGateway.model.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotBlank(message = "Client Order id is required")
    private String clientOrderId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "side is required")
    private OrderSide side;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0",inclusive = false,message = "price must be positive")
    private BigDecimal price;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.0",inclusive = false,message = "quantity must be positive")
    private BigDecimal quantity;


}

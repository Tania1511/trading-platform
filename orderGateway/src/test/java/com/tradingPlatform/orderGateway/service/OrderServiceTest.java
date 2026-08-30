package com.tradingPlatform.orderGateway.service;

import com.tradingPlatform.orderGateway.cache.IdempotencyCacheService;
import com.tradingPlatform.orderGateway.dto.OrderResponse;
import com.tradingPlatform.orderGateway.dto.PlaceOrderRequest;
import com.tradingPlatform.orderGateway.event.OrderEventPublisher;
import com.tradingPlatform.orderGateway.model.Order;
import com.tradingPlatform.orderGateway.model.OrderSide;
import com.tradingPlatform.orderGateway.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @Mock
    private IdempotencyCacheService idempotencyCacheService;

    @InjectMocks
    private OrderService orderService;

    private PlaceOrderRequest request;

    @BeforeEach
    void setup(){
        request = new PlaceOrderRequest();
        request.setClientOrderId("client-order-001");
        request.setSymbol("AAPL");
        request.setSide(OrderSide.BUY);
        request.setPrice(new BigDecimal("150.25"));
        request.setQuantity(new BigDecimal("10"));
    }

    @Test
    void placeOrder_newClientOrderId_saveAndReturnNewOrder(){

        when(idempotencyCacheService.getCachedResponse("client-order-001")).thenReturn(Optional.empty());
        when(orderRepository.findByClientOrderId("client-order-001")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.placeOrder(request);

        assertThat(response.getSymbol()).isEqualTo("AAPL");
        assertThat(response.getClientOrderId()).isEqualTo("client-order-001");

        verify(orderRepository,times(1)).save(any(Order.class));

        verify(orderEventPublisher, times(1)).publishOrderPlaced(any(Order.class));

        verify(idempotencyCacheService, times(1)).cacheResponse(eq("client-order-001"),any(OrderResponse.class));
    }

    @Test
    void placeOrder_duplicateClientOrderId_databaseFallback_doesNotCreateSecondOrder(){

        when(idempotencyCacheService.getCachedResponse("client-order-001")).thenReturn(Optional.empty());

        Order existingOrder = new Order("client-order-001","AAPL",OrderSide.BUY,new BigDecimal("150.25"),new BigDecimal("10"));
        when(orderRepository.findByClientOrderId("client-order-001")).thenReturn(Optional.of(existingOrder));

        OrderResponse response = orderService.placeOrder(request);

        assertThat(response.getClientOrderId()).isEqualTo("client-order-001");

        verify(orderRepository,never()).save(any(Order.class));

        verify(orderEventPublisher, never()).publishOrderPlaced(any(Order.class));

        verify(idempotencyCacheService, times(1)).cacheResponse(eq("client-order-001"),any(OrderResponse.class));
    }

    @Test
    void placeOrder_cacheHit_skipsDatabaseEntirely() {
        OrderResponse cacheResponse = OrderResponse.from(new Order("client-order-001","AAPL",OrderSide.BUY,new BigDecimal("150.25"),new BigDecimal("10")));
        when(idempotencyCacheService.getCachedResponse("client-order-001")).thenReturn(Optional.of(cacheResponse));

        OrderResponse response = orderService.placeOrder(request);

        assertThat(response.getClientOrderId()).isEqualTo("client-order-001");

        verify(orderRepository, never()).findByClientOrderId(anyString());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderEventPublisher, never()).publishOrderPlaced(any(Order.class));
        verify(idempotencyCacheService, never()).cacheResponse(anyString(),any(OrderResponse.class));
    }

    @Test
    void cancelOrder_alreadyFilledOrder_throwsIllegalStateException(){
        Order filledOrder = new Order("client-order-001","AAPL",OrderSide.BUY,new BigDecimal("150.25"),new BigDecimal("10"));
        filledOrder.cancel();

        when(orderRepository.findById(any())).thenReturn(Optional.of(filledOrder));
        assertThatThrownBy(() -> orderService.cancelOrder(filledOrder.getId())).isInstanceOf(IllegalStateException.class);
        verify(orderRepository,never()).save(any(Order.class));
    }
}

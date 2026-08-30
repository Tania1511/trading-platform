package com.tradingPlatform.orderGateway.service;

import com.tradingPlatform.orderGateway.cache.IdempotencyCacheService;
import com.tradingPlatform.orderGateway.dto.OrderResponse;
import com.tradingPlatform.orderGateway.dto.PlaceOrderRequest;
import com.tradingPlatform.orderGateway.event.OrderEventPublisher;
import com.tradingPlatform.orderGateway.model.Order;
import com.tradingPlatform.orderGateway.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final IdempotencyCacheService idempotencyCacheService;

    public OrderService (OrderRepository orderRepository,OrderEventPublisher orderEventPublisher, IdempotencyCacheService idempotencyCacheService) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.idempotencyCacheService = idempotencyCacheService;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request){
        String clientOrderId = request.getClientOrderId();
        Optional<OrderResponse> cached = idempotencyCacheService.getCachedResponse(clientOrderId);
        if(cached.isPresent()){
            return cached.get();
        }
        Optional<Order> existingOrder = orderRepository.findByClientOrderId(request.getClientOrderId());

        if(existingOrder.isPresent()){
            idempotencyCacheService.cacheResponse(request.getClientOrderId(), OrderResponse.from(existingOrder.get()));
            return OrderResponse.from(existingOrder.get());
        }

        Order newOrder = new Order(
                request.getClientOrderId(), request.getSymbol(), request.getSide(), request.getPrice(),request.getQuantity()
        );
        Order savedOrder = orderRepository.save(newOrder);
        orderEventPublisher.publishOrderPlaced(savedOrder);
        idempotencyCacheService.cacheResponse(request.getClientOrderId(), OrderResponse.from(savedOrder));
        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.cancel();
        return OrderResponse.from(order);
    }
}

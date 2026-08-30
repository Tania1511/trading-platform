package com.tradingPlatform.orderGateway.Controller;

import com.tradingPlatform.orderGateway.dto.OrderResponse;
import com.tradingPlatform.orderGateway.dto.PlaceOrderRequest;
import com.tradingPlatform.orderGateway.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService service) {
        this.orderService = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request, UriComponentsBuilder uribuilder){
        OrderResponse orderResponse = orderService.placeOrder(request);
        var location = uribuilder.path("/orders/{id}").buildAndExpand(orderResponse.getId()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(orderResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id){
        OrderResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id){
        OrderResponse response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }
}

package com.tradingPlatform.orderGateway.cache;

import tools.jackson.core.JacksonException;

import com.tradingPlatform.orderGateway.config.AppProperties;
import com.tradingPlatform.orderGateway.dto.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
public class IdempotencyCacheService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyCacheService.class);

    private static final String KEY = "idempotency:";
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;

    public IdempotencyCacheService(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate, AppProperties appProperties){
        this.objectMapper=objectMapper;
        this.redisTemplate=stringRedisTemplate;
        this.appProperties=appProperties;
    }

    public Optional<OrderResponse> getCachedResponse(String clientOrderId){
       String json = redisTemplate.opsForValue().get(KEY + clientOrderId);

       if(json==null)
           return Optional.empty();

       try{
           return Optional.of(objectMapper.readValue(json, OrderResponse.class));
       }catch(JacksonException ex){
           log.warn("Failed to deserialize cached idempotency response for clientOrderId={}, treating as cache miss",
                   clientOrderId,ex);
       }

       return Optional.empty();
    }

    public void cacheResponse(String clientOrderId, OrderResponse orderResponse){
        try{
            String json = objectMapper.writeValueAsString(orderResponse);
            Duration ttl = Duration.ofSeconds(appProperties.getIdempotencyCacheTtlSeconds());
            redisTemplate.opsForValue().set(KEY+clientOrderId, json,ttl);
        }catch(JacksonException ex){
            log.warn("Failed to cache idempotency response for clientId={}",clientOrderId,ex);
        }
    }



}

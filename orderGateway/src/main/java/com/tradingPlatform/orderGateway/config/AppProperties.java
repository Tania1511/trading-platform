package com.tradingPlatform.orderGateway.config;


import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Component
@Validated
public class AppProperties {

    @NotNull
    private long idempotencyCacheTtlSeconds = 300;


    public long getIdempotencyCacheTtlSeconds() {
        return idempotencyCacheTtlSeconds;
    }

    public void setIdempotencyCacheTtlSeconds(long idempotencyCacheTtlSeconds) {
        this.idempotencyCacheTtlSeconds = idempotencyCacheTtlSeconds;
    }
}

package com.tradingplatform.surveillance_service.detector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "surveillance.spoofing")
@Component
public class SpoofingDetectionProperties {

    private BigDecimal largeQuantityThreshold = new BigDecimal("50");
    private BigDecimal maxfillRatio = new BigDecimal("0.10"); //10%
    private long maxSecondsToCancel = 30;

    public BigDecimal getLargeQuantityThreshold() {
        return largeQuantityThreshold;
    }

    public void setLargeQuantityThreshold(BigDecimal largeQuantityThreshold) {
        this.largeQuantityThreshold = largeQuantityThreshold;
    }

    public BigDecimal getMaxfillRatio() {
        return maxfillRatio;
    }

    public void setMaxfillRatio(BigDecimal maxfillRatio) {
        this.maxfillRatio = maxfillRatio;
    }

    public long getMaxSecondsToCancel() {
        return maxSecondsToCancel;
    }

    public void setMaxSecondsToCancel(long maxSecondsToCancel) {
        this.maxSecondsToCancel = maxSecondsToCancel;
    }
}

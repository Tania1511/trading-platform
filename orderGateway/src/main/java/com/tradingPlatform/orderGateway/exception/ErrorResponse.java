package com.tradingPlatform.orderGateway.exception;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }


}

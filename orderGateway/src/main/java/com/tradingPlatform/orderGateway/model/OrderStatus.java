package com.tradingPlatform.orderGateway.model;

public enum OrderStatus {
    NEW,               // The order has been accepted by our system but has not been matched yet.
    PARTIALLY_FILLED,  // Part of the order's quantity has been matched/traded, but not all of it.  // Example: you placed an order for 100 shares, 40 have traded, 60 remain open.
    FILLED,            // The entire quantity of the order has been matched/traded. Terminal state.
    CANCELED,          // The order was canceled before being fully filled. Terminal state.
    REJECTED          // The order was rejected by validation (e.g. invalid symbol, negative price) and never entered the order book at all. Terminal state.
}

CREATE table processed_trade_event(
    event_id        UUID PRIMARY KEY,
    processed_at    TIMESTAMP NOT NULL
);
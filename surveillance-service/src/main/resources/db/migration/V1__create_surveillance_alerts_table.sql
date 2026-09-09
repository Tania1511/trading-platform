CREATE TABLE surveillance_alerts(

    id                  UUID PRIMARY KEY,
    orderId             UUID NOT NULL,
    clientOrderId       VARCHAR(255) NOT NULL,
    symbol              VARCHAR(255) NOT NULL,
    original_quantity    NUMERIC(19,4) NOT NULL,
    filled_quantity     NUMERIC(19,4) NOT NULL,
    fill_ratio          NUMERIC(6,4) NOT NULL,
    seconds_to_cancel   BIGINT NOT NULL,
    ai_explanation      TEXT,
    created_at          TIMESTAMP
);

CREATE INDEX idx_surveillance_alerts_created_at ON surveillance_alerts (created_at DESC);
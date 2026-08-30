CREATE TABLE orders (

    id              UUID PRIMARY KEY,
    client_order_id VARCHAR (255) NOT NULL,
    symbol          VARCHAR (255) NOT NULL,
    side            VARCHAR (255) NOT NULL,
    price           NUMERIC(19,4) NOT NULL,
    quantity        NUMERIC(19,4) NOT NULL,
    filled_quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    status          VARCHAR (255) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0

);

ALTER TABLE orders ADD CONSTRAINT uk_orders_client_order_id UNIQUE (client_order_id);

CREATE INDEX idx_orders_symbol on orders (symbol);
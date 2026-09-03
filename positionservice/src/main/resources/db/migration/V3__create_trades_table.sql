CREATE TABLE trades (
     id                         UUID PRIMARY KEY,
     trade_id                   UUID NOT NULL,
     symbol                     VARCHAR(250) NOT NULL,
     buy_client_order_id        VARCHAR(250) NOT NULL,
     sell_client_order_id       VARCHAR(250) NOT NULL,
     price                      NUMERIC(19,4) NOT NULL,
     quantity                   NUMERIC(19,4) NOT NULL,
     occurred_at                TIMESTAMP NOT NULL
);

ALTER TABLE trades ADD CONSTRAINT uk_trade_trade_id_unique UNIQUE (trade_id);


CREATE INDEX  idx_trades_occurred_at ON trades (occurred_at DESC);
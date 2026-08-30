CREATE TABLE positions (
    id              UUID PRIMARY KEY,
    account_key     VARCHAR(255) NOT NULL,
    symbol          VARCHAR(255) NOT NULL,
    quantity        NUMERIC(19,4) NOT NULL,
    average_cost    NUMERIC(19,4) NOT NULL,
    realized_pnl    NUMERIC(19,4) NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE positions ADD CONSTRAINT uk_positions_account_symbol UNIQUE (account_key,symbol)
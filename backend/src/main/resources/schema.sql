-- 1. users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. holdings
CREATE TABLE IF NOT EXISTS holdings (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    ticker_symbol VARCHAR(50) NOT NULL,
    asset_type VARCHAR(50) NOT NULL, -- Enum: STOCK, ETF, MUTUAL_FUND
    quantity NUMERIC(19, 4) NOT NULL,
    purchase_price NUMERIC(19, 4) NOT NULL,
    purchase_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sector VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_holdings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. price_history
-- Normalization Justification: 
-- We separate price_history from holdings because a holding represents a user's 
-- position in an asset at a specific cost basis. The current market price of a 
-- ticker changes constantly and is independent of the user's holding. 
-- By normalizing this into price_history, we can:
--   1. Store historical price trends over time (time-series data).
--   2. Reuse the same ticker's price data across multiple users who hold the same asset.
--   3. Avoid updating the holdings table every time a market price changes, 
--      which would cause unnecessary locking and write amplification.
CREATE TABLE IF NOT EXISTS price_history (
    id UUID PRIMARY KEY,
    ticker_symbol VARCHAR(50) NOT NULL,
    price NUMERIC(19, 4) NOT NULL,
    recorded_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    source VARCHAR(100) DEFAULT 'alphavantage' NOT NULL
);

-- 4. portfolio_snapshots
CREATE TABLE IF NOT EXISTS portfolio_snapshots (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    total_value NUMERIC(19, 4) NOT NULL,
    snapshot_date DATE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_snapshots_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_holdings_user_id ON holdings(user_id);
CREATE INDEX IF NOT EXISTS idx_price_history_ticker_recorded ON price_history(ticker_symbol, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_portfolio_snapshots_user_date ON portfolio_snapshots(user_id, snapshot_date);

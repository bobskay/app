CREATE TABLE account_history (
    id BIGINT NOT NULL PRIMARY KEY,
    account_info VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP
);

CREATE TABLE config (
    id BIGINT NOT NULL PRIMARY KEY,
    config_key VARCHAR(2000) NOT NULL,
    config_content CLOB
);

CREATE TABLE user_info (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(32),
    pwd VARCHAR(32),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trace_info (
    id BIGINT NOT NULL PRIMARY KEY,
    buy_id VARCHAR(64),
    sell_id VARCHAR(64),
    buy_price DECIMAL(20,8) DEFAULT 0.00000000,
    sell_price DECIMAL(20,8) DEFAULT 0.00000000,
    quantity DECIMAL(20,8) DEFAULT 0.00000000,
    profit DECIMAL(20,8) DEFAULT 0.00000000,
    duration_seconds BIGINT DEFAULT 0,
    buy_start TIMESTAMP,
    sell_start TIMESTAMP,
    sell_end TIMESTAMP,
    trace_state VARCHAR(32)
);
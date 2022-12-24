CREATE TABLE account_info (
    id bigint(20) NOT NULL COMMENT '主键ID',
    hold decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '持仓',
    avg_price decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '总成本',
    currency varchar(32) DEFAULT NULL COMMENT '货币种类',
    buy_prices varchar(2000) DEFAULT NULL COMMENT '最近买单成交价',
    confirm_price decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '最近的一次成交价',
    last_buy datetime DEFAULT NULL COMMENT '最后买入',
    last_sell datetime DEFAULT NULL COMMENT '最后卖出',
    PRIMARY KEY (id)
) COMMENT '用户账户';

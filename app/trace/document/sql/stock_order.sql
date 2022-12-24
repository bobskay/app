CREATE TABLE stock_order (
    id bigint(20) NOT NULL COMMENT '主键ID',
    symbol varchar(32) DEFAULT NULL COMMENT '交易对',
    client_order_id varchar(32) DEFAULT NULL COMMENT '自定义id',
    price decimal(20,8) NOT NULL DEFAULT '0.0000' COMMENT '价格',
    type varchar(32) DEFAULT NULL COMMENT '订单类型,filled',
    quantity decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '交易数量',
    order_side varchar(4) DEFAULT NULL COMMENT '交易方向',
    finish decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '完成据量',
    created_at datetime DEFAULT NULL COMMENT '时间',
    ori varchar(1000) DEFAULT NULL COMMENT '原始信息',
    PRIMARY KEY (id)
) COMMENT '成交订单';
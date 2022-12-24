CREATE TABLE trace_order (
    id bigint(20) NOT NULL COMMENT '主键ID',
    business_id bigint(20) NOT NULL COMMENT '对应业务id',
    symbol varchar(32) DEFAULT NULL COMMENT '交易对',
    client_order_id varchar(32) DEFAULT NULL COMMENT '自定义id',
    expect_price decimal(20,8) NOT NULL DEFAULT '0.0000' COMMENT '下单价',
    trace_order_type varchar(16) DEFAULT NULL COMMENT '订单类型',
    price decimal(20,8) NOT NULL DEFAULT '0.0000' COMMENT '价格',
    quantity decimal(20,8) NOT NULL DEFAULT '0.0000' COMMENT '交易数量',
    order_side varchar(4) DEFAULT NULL COMMENT '交易方向',
    created_at datetime DEFAULT NULL COMMENT '时间',
    finish_at datetime DEFAULT NULL COMMENT '时间',
    updated_at datetime DEFAULT NULL COMMENT '时间',
    order_state varchar(16) DEFAULT NULL COMMENT '订单状态',
    PRIMARY KEY (id)
) COMMENT '待交易订单';


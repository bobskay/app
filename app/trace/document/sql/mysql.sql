CREATE TABLE account_history (
    id bigint(20) NOT NULL COMMENT '主键ID',
    account_info varchar(2000) NOT NULL COMMENT '账户信息',
    created_at datetime DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (id)
) COMMENT '账户信息';

CREATE TABLE config (
    id bigint(20) NOT NULL COMMENT '主键ID',
    config_key varchar(2000) NOT NULL COMMENT '',
    config_content text  NULL COMMENT '',
    PRIMARY KEY (id)
) COMMENT '配置';


CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  pwd varchar(32) DEFAULT NULL COMMENT '密码',
  `created_at` datetime default CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trace_info` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `buy_id` varchar(64)   NULL,
  `sell_id` varchar(64)   NULL,
  `buy_price` decimal(20,8)  NULL DEFAULT '0.00000000' COMMENT '价格',
  `sell_price` decimal(20,8)  NULL DEFAULT '0.00000000' COMMENT '价格',
  `quantity` decimal(20,8)  NULL DEFAULT '0.00000000' COMMENT '数量',
  `profit` decimal(20,8)  NULL DEFAULT '0.00000000' COMMENT '价格',
  `duration_seconds` bigint  NULL DEFAULT 0 COMMENT '耗时',
  `buy_start` datetime DEFAULT NULL COMMENT '买入开始',
  `sell_start` datetime DEFAULT NULL COMMENT '卖出开始',
  `sell_end` datetime DEFAULT NULL COMMENT '卖出结束',
  `trace_state` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '订单状态',
  PRIMARY KEY (`id`)
)  COMMENT='交易信息';
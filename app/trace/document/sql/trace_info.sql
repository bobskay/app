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
CREATE TABLE `trace_order` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `business_id` bigint NOT NULL COMMENT '对应业务id',
  `ref_id` bigint  NULL COMMENT '关联订单id',
  `symbol` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '交易对',
  `client_order_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '自定义id',
  `expect_price` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '下单价',
  `trace_order_type` varchar(16) COLLATE utf8_bin DEFAULT NULL COMMENT '订单类型',
  `price` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格',
  `quantity` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易数量',
  `order_side` varchar(4) COLLATE utf8_bin DEFAULT NULL COMMENT '交易方向',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `finish_at` datetime DEFAULT NULL COMMENT '完成时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `order_state` varchar(16) COLLATE utf8_bin DEFAULT NULL COMMENT '订单状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='待交易订单'
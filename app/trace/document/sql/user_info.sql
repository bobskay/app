CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  pwd varchar(32) DEFAULT NULL COMMENT '密码',
  `created_at` datetime default CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
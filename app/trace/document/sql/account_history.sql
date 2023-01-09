CREATE TABLE account_history (
    id bigint(20) NOT NULL COMMENT '主键ID',
    accountInfo varchar(2000) NOT NULL COMMENT '账户信息',
    created_at datetime DEFAULT NULL COMMENT '时间',
    PRIMARY KEY (id)
) COMMENT '账户信息';


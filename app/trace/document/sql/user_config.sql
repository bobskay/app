CREATE TABLE user_config (
    id bigint(20) NOT NULL COMMENT '主键ID',
    config_name varchar(30) DEFAULT NULL COMMENT '名称',
    content varchar(2000) DEFAULT NULL COMMENT '内容',
    PRIMARY KEY (id)
) COMMENT '用户配置';
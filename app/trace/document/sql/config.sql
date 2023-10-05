CREATE TABLE config (
    id bigint(20) NOT NULL COMMENT '主键ID',
    config_key varchar(2000) NOT NULL COMMENT '',
    config_content text  NULL COMMENT '',
    PRIMARY KEY (id)
) COMMENT '配置';

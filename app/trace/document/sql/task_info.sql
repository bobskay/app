CREATE TABLE task_info (
    id bigint(20) NOT NULL COMMENT '主键ID',
    data varchar(2000) NOT NULL COMMENT '策略配置,根据具体策略保存不同策略的json',
    interval_second int DEFAULT NULL COMMENT '任务执行间隔',
    created_at datetime DEFAULT NULL COMMENT '时间',
    next_at datetime DEFAULT NULL COMMENT '时间',
    updated_at datetime DEFAULT NULL COMMENT '时间',
    strategy varchar(20) NOT NULL COMMENT '任务策略',
    task_state varchar(20) NOT NULL COMMENT '任务状态',
    error_count int DEFAULT NULL COMMENT '错误次数',
    run_count int DEFAULT NULL COMMENT '执行次数',
    max_error int DEFAULT NULL COMMENT '允许的最大错误次数',
    remark varchar(2000)  NULL COMMENT '备注',
    PRIMARY KEY (id)
) COMMENT '交易任务';

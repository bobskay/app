package a.b.c.trace.model;

import a.b.c.base.annotation.Remark;
import a.b.c.trace.enums.TaskState;
import lombok.Data;

import java.util.Date;

@Data
public class TaskInfo {

    private Long id;

    @Remark("策略配置,根据具体策略保存不同策略的json")
    private String data;

    @Remark("执行间隔")
    private Integer intervalSecond;

    private TaskState taskState;

    private Date createdAt;
    private Date updatedAt;
    private Date nextAt;
    private String strategy;
    @Remark("错误次数")
    private Integer errorCount;
    @Remark("允许的最大错误次数")
    private Integer maxError;
    private String remark;
    private Integer runCount;

}

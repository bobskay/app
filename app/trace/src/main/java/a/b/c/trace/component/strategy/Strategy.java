package a.b.c.trace.component.strategy;

import a.b.c.trace.model.TaskInfo;

public interface Strategy {
    String TUN_BI_BAO = "tunBiBao";
    String WANG_GE = "wangGe";

    void run(TaskInfo taskInfo);

    //和远程同步
    TaskInfo sync(TaskInfo taskInfo);
}

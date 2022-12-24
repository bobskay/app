package a.b.c.trace.model.vo;

import a.b.c.trace.model.TaskInfo;
import lombok.Data;

@Data
public class TaskInfoVo extends TaskInfo {

    public Integer getNextRemain() {
        if (getNextAt() == null) {
            return -1;
        }
        long diff = getNextAt().getTime() - System.currentTimeMillis();
        return (int) diff / 1000;
    }
}

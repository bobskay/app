package a.b.c.trace.model.vo;

import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.model.TaskInfo;
import lombok.Data;

import java.util.List;

@Data
public class TaskInfoVo extends TaskInfo {

    /**
     * data里对应的java对象
     */
    private Object dataObj;

    private List<OpenOrder> openOrders;

    public Integer getNextRemain() {
        if (getNextAt() == null) {
            return -1;
        }
        long diff = getNextAt().getTime() - System.currentTimeMillis();
        return (int) diff / 1000;
    }
}

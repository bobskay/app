package a.b.c.trace.component.strategy.vo;

import a.b.c.trace.model.TraceOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WangGeOrders {
    private WangGeOrder sell2;
    private WangGeOrder sell1;
    private WangGeOrder buy1;
    private WangGeOrder buy2;
}

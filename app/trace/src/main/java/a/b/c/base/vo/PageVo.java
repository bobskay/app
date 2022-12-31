package a.b.c.base.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageVo<T> {

    private List<T> records;
    private int total;
    private int currentPage;

    public PageVo(IPage<T> iPage){
        this.records=iPage.getRecords();
        this.total= (int) iPage.getTotal();
        this.currentPage= (int) iPage.getCurrent();
    }
}

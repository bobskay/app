package a.b.c.base.dto;

import a.b.c.Constant;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页的查询如参，查询到时候结束时间会加上999ms
 */
@Data
public class PageQueryDto<T> implements Serializable {
    private Integer pageNo;
    private Integer pageSize;

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> toPage() {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? Constant.DEFAULT_QUERY_PAGE_SIZE : pageSize;
        if (pageSize > Constant.MAX_QUERY_PAGE_SIZE) {
            pageSize = Constant.MAX_QUERY_PAGE_SIZE;
        }
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page(pageNo, pageSize);
    }
}

package a.b.c.base.dto;

import a.b.c.Constant;
import lombok.Data;

@Data
public class PageDto {

  private Integer pageNum=0;
  private Integer pageSize= Constant.DEFAULT_PAGE_SIZE;

}

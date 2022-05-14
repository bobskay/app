package a.b.c.demo.model.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookInsertDto {

  @NotNull(message = "图书名称不能为空")
  private String bookName;
}

package a.b.c.demo.model;

import a.b.c.base.dao.Model;
import java.util.Date;
import lombok.Data;

@Data
public class Book implements Model {

  private Long id;
  private String bookName;
  private String isbn;
  private Date createdAt;
  private Date updatedAt;
}

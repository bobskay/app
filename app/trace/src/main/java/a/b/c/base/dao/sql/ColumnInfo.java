package a.b.c.base.dao.sql;

import a.b.c.base.annotation.Remark;
import lombok.Data;

@Data
public class ColumnInfo {

  @Remark("字段名")
  private String field;
  @Remark("字段类型,带长度，例如varchar(20)")
  private String type;
  private String collation;
  private String nulL;
  private String key;
  @Remark("默认值")
  private String defaulT;
  private String extra;
  private String privileges;
  private String comment;

}

package a.b.c.base.dao.id;

import a.b.c.base.dao.sql.ColumnInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TableInfoMapper {

  /**
   * 查询字段信息，获取字段默认值
   */
  @Select("show full columns from ${tableName}")
  List<ColumnInfo> getColumns(String tableName);
}

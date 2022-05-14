package a.b.c.base.dao;

import a.b.c.base.annotation.Remark;
import java.io.Serializable;

/**
 * 所有model都要实现的接口，用于自动生成sql
 *
 * @author huawei
 */
public interface Model extends Serializable {

  @Remark("设置主键")
  Long getId();

  @Remark("获得主键")
  void setId(Long id);
}

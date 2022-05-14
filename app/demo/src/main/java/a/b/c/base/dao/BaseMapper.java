package a.b.c.base.dao;

import a.b.c.base.dao.sql.SqlProvider;
import a.b.c.base.utils.ClassUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * mapper的基类，继承这个后实现新增和简单查询
 *
 * @author huawei
 */
public interface BaseMapper<T extends Model> {

  /**
   * 通过字段值查询，返回值最多只能有1条
   *
   * @param model 查询条件，包含主键
   * @return
   */
  @SelectProvider(type = SqlProvider.class, method = "selectByModel")
  T unique(T model);

  /**
   * 通过字段值查询，返回list
   *
   * @param model 查询条件，包含主键
   * @return
   */
  @SelectProvider(type = SqlProvider.class, method = "selectByModel")
  List<T> selectList(T model);

  /**
   * 新增
   *
   * @param model 要新增的数据
   * @return 新增条数
   */
  @InsertProvider(type = SqlProvider.class, method = "insert")
  int insert(T model);

  @InsertProvider(type = SqlProvider.class, method = "update")
  int updateNotNullById(T model);

  /**
   * 通过id查询
   *
   * @param id 主键
   * @return
   */
  default T getById(Long id) {
    T model = ClassUtil.newInstance(modelClass());
    model.setId(id);
    return unique(model);
  }

  /**
   * 获得当前接口对应的model
   *
   * @return
   */
  default Class<T> modelClass() {
    Class mapper = (Class) this.getClass().getGenericInterfaces()[0];
    Type type = mapper.getGenericInterfaces()[0];
    while (type instanceof Class) {
      type = ((Class) type).getGenericInterfaces()[0];
    }
    ParameterizedType baseMapper = (ParameterizedType) type;
    return (Class) baseMapper.getActualTypeArguments()[0];

  }
}

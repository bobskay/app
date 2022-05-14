package a.b.c.base.dao;


/**
 * 单表的查询条件，
 * <P>如果字段不为空就加入条件，默认用=
 * <p>如果字段以start结尾就用>=
 * <p>如果字段以end结尾就用<=
 *
 * @author huawei
 */
public interface Selector<T extends Model> {

}

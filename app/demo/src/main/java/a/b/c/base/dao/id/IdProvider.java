package a.b.c.base.dao.id;

import java.util.Set;

/**
 * 创建可用id
 */
public interface IdProvider {

  Set<Long> nexId(Integer count);
}

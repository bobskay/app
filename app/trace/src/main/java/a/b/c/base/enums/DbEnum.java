package a.b.c.base.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 保存到数据库枚举，入库到值为ordinal，所以顺序很重要
 */
public interface DbEnum extends IEnum<Integer> {

}

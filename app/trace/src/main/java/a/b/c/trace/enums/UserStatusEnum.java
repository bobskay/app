package a.b.c.trace.enums;

import a.b.c.base.enums.DbEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum implements DbEnum {
    AUDIT(1, "待审核"), NORMAL(0, "正常"), FROZEN(2, "冻结"), DISABLE(3, "停用");

    private final Integer value;
    private final String name;

}
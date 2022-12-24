package a.b.c.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum implements DbEnum {
    U(0,"未知"),M(1,"男"), F(2,"女"), ;

    private final Integer value;
    private final String name;

}

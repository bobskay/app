package a.b.c.trace.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Currency {
    BTC(5,2),
    ETH(4,2),
    PEOPLE(1,6),
    USDT(1,2),
    BNB(3,1),
    ONE(1,4),
    GALA(0,2),
    FIL(2,3),
    DOT(2,2);

    public String usdt() {
        return this + "USDT";
    }

    public String busd() {
        return this + "BUSD";
    }

    private Integer quantityScale;
    private Integer scale;

}

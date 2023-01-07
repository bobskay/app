package a.b.c.trace.enums;

public enum Currency {
    BTC, ETH, PEOPLE, USDT, BNB, ONE, GALA,FIL,DOT;

    public String usdt() {
        return this + "USDT";
    }

    public String busd() {
        return this + "BUSD";
    }


    public Integer quantityScale() {
        switch (this) {
            case ETH:
                return 4;
            case DOT:
            case FIL:
                return 2;
            case BNB:
                return 3;
            case BTC:
                return 5;
            case USDT:
            case PEOPLE:
            case ONE:
                return 1;
            case GALA:
                return 0;
        }
        throw new RuntimeException("暂不支持:" + this);
    }

    public Integer scale() {
        switch (this) {
            case ETH:
            case BTC:
            case USDT:
            case DOT:
                return 2;
            case FIL:
                return 3;
            case ONE:
                return 4;
            case GALA:
                return 5;
            case BNB:
                return 1;
            case PEOPLE:
                return 6;
        }
        throw new RuntimeException("暂不支持:" + this);
    }
}

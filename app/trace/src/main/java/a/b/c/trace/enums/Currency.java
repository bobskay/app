package a.b.c.trace.enums;

public enum Currency {
    BTC, ETH, PEOPLE,USDT;

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
            case BTC:
                return 5;
            case USDT:
                return 2;
            case PEOPLE:
                return 1;
        }
        throw new RuntimeException("暂不支持:" + this);
    }

    public Integer scale() {
        switch (this) {
            case ETH:
            case BTC:
            case USDT:
                return 2;
            case PEOPLE:
                return 6;
        }
        throw new RuntimeException("暂不支持:" + this);
    }
}

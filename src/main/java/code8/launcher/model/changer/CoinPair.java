package code8.launcher.model.changer;

/**
 * todo: javadoc
 */
public enum CoinPair {
    BTCvsUSD,
    ETHvsUSD,
    XRPvsUSD,
    ETHvsBTC,
    XRPvsBTC;

    final Coin base, counter;

    CoinPair() {
        String[] parts = name().split("vs");
        this.base = Coin.valueOf(parts[0]);
        this.counter = Coin.valueOf(parts[1]);
    }

    @Override
    public String toString() {
        return base.name() + "/" + counter.name();
    }

    public Coin getBase() {
        return base;
    }

    public Coin getCounter() {
        return counter;
    }
}

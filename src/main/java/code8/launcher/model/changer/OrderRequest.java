package code8.launcher.model.changer;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class OrderRequest {
    public enum Type {
        Bid, Ask
    }

    Type type;
    CoinPair pair;
    BigDecimal rate;
    BigDecimal volume;
    long accountId;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public CoinPair getPair() {
        return pair;
    }

    public void setPair(CoinPair pair) {
        this.pair = pair;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}

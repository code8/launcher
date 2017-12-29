package code8.launcher.model.changer;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class Wallet {
    long id;
    long accountId;
    String address;
    Coin coin;
    BigDecimal balance = BigDecimal.ZERO;

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getAddress() {
        return address;
    }

    public Coin getCoin() {
        return coin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

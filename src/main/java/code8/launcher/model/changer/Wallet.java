package code8.launcher.model.changer;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class Wallet {
    long id;
    final long accountId;
    final String address;
    final Coin coin;
    BigDecimal balance = BigDecimal.ZERO;

    public Wallet(long accountId, String address, Coin coin) {
        this.accountId = accountId;
        this.address = address;
        this.coin = coin;
    }

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

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

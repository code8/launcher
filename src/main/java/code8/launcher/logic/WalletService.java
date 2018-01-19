package code8.launcher.logic;

import code8.launcher.model.Coin;
import code8.launcher.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * todo: javadoc
 */
public interface WalletService {
    Wallet getAccountCoinWallet(long accountId, Coin coin);
    BigDecimal getWalletBalance(long walletId);
    void changeBalance(List<WalletBalanceChange> changes);

    class WalletNotFoundException extends RuntimeException {
        public WalletNotFoundException(long walletId) {
            super("Wallet not found by id:" + walletId);
        }
    }

    class FundsNotEnoughException extends RuntimeException {
        public FundsNotEnoughException(long walletId) {
            super("Not enough funds in wallet:" + walletId);
        }
    }

    class WalletBusyException extends RuntimeException {
        public WalletBusyException(long walletId) {
            super("Can`t lock the wallet:" + walletId);
        }
    }

    class WalletBalanceChange {
        final long walletId;
        final BigDecimal amountDelta;

        public WalletBalanceChange(long walletId, BigDecimal amountDelta) {
            this.walletId = walletId;
            this.amountDelta = amountDelta;
        }

        public long getWalletId() {
            return walletId;
        }

        public BigDecimal getAmountDelta() {
            return amountDelta;
        }
    }
}

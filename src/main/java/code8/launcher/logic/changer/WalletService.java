package code8.launcher.logic.changer;

import code8.launcher.model.changer.Coin;
import code8.launcher.model.changer.Wallet;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public interface WalletService {
    Wallet getWallet(long accountId, Coin coin);
    Wallet getWallet(long walletId);
    void changeBalance(long walletId, BigDecimal amount);

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
}

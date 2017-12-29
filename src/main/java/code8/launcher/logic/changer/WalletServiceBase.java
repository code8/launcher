package code8.launcher.logic.changer;

import code8.launcher.model.changer.Coin;
import code8.launcher.model.changer.Wallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * todo: javadoc
 */
@Service
public class WalletServiceBase implements WalletService {
    protected ConcurrentMap<Long, Wallet> walletsById = new ConcurrentHashMap<>();
    protected ConcurrentMap<Long, ConcurrentMap<Coin,Wallet>> accountWallets = new ConcurrentHashMap<>();


    @Override
    public Wallet getWallet(long accountId, Coin coin) {
        Map<Coin,Wallet> wallets = accountWallets.computeIfAbsent(accountId, id -> new ConcurrentHashMap<>());
        return wallets.computeIfAbsent(coin, c -> makeWallet(accountId, c));
    }

    @Override
    public Wallet getWallet(long walletId) {
        Wallet wallet = walletsById.get(walletId);

        if (wallet == null) {
            throw new WalletNotFoundException(walletId);
        }

        return wallet;
    }

    @Override
    public void changeBalance(long walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId);
        synchronized (wallet) {
            BigDecimal balance = wallet.getBalance().add(amount);
            if (balance.compareTo(BigDecimal.ZERO ) < 0) {
                throw new FundsNotEnoughException(walletId);
            }
            wallet.setBalance(balance);
        }
    }

    private Wallet makeWallet(long accountId, Coin coin) {
        Wallet wallet = new Wallet();

        wallet.setCoin(coin);
        wallet.setAccountId(accountId);

        persist(fillAddress(wallet));
        walletsById.put(wallet.getId(), wallet);

        return wallet;
    }

    protected Wallet persist(Wallet wallet) {
        wallet.setId(ThreadLocalRandom.current().nextLong());
        return wallet;
    }

    protected Wallet fillAddress(Wallet wallet) {
        return wallet;
    }
}

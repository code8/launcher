package code8.launcher.logic.changer;

import code8.launcher.model.changer.Coin;
import code8.launcher.model.changer.Wallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * todo: javadoc
 */
@Service
public class WalletServiceBase implements WalletService {
    private static final int WALLET_TRY_LOCK_COUNT = 10;

    protected ConcurrentMap<Long, Wallet> walletsById = new ConcurrentHashMap<>();
    protected ConcurrentMap<Long, ConcurrentMap<Coin,Wallet>> accountWallets = new ConcurrentHashMap<>();
    protected ConcurrentMap<Long, Wallet> lockedWallets = new ConcurrentHashMap<>();

    @Override
    public Wallet getAccountCoinWallet(long accountId, Coin coin) {
        Map<Coin,Wallet> wallets = accountWallets.computeIfAbsent(accountId, id -> new ConcurrentHashMap<>());
        return wallets.computeIfAbsent(coin, c -> makeWallet(accountId, c));
    }

    @Override
    public BigDecimal getWalletBalance(long walletId) {
        return getWallet(walletId).getBalance();
    }

    @Override
    public void changeBalance(List<WalletBalanceChange> changes) {
        List<Long> wallets = changes
                .stream()
                .map(WalletBalanceChange::getWalletId)
                .sorted()
                .collect(Collectors.toList());

        long busyWallet;

        if ((busyWallet = tryLockWallets(wallets, WALLET_TRY_LOCK_COUNT)) != 0) {
            throw new WalletBusyException(busyWallet);
        }

        int processed = 0;

        try {
            for (WalletBalanceChange change : changes) {
                Wallet wallet = getWallet(change.getWalletId());
                BigDecimal balance = wallet.getBalance().add(change.getAmountDelta());
                if (balance.compareTo(BigDecimal.ZERO ) < 0) {
                    throw new FundsNotEnoughException(wallet.getId());
                }
                wallet.setBalance(balance);
                processed++;
            }

        } catch (Exception ex) {
            // rollback
            if (processed < changes.size()) {
                while (processed-- > 0) {
                    WalletBalanceChange change = changes.get(processed);
                    Wallet wallet = getWallet(change.getWalletId());
                    BigDecimal balance = wallet.getBalance().subtract(change.getAmountDelta());
                    wallet.setBalance(balance);
                }
            }
            throw ex;
        } finally {
            unlock(wallets);
        }
    }

    private long tryLockWallets(List<Long> wallets, int tryCount) {
        boolean done = false;

        for (Long walletId : wallets) {
            int walletTryCount = tryCount;

            while (walletTryCount-- > 0) {
                done = lockedWallets.putIfAbsent(walletId, getWallet(walletId)) != null;
            }

            if (!done) {
                return walletId;
            }
        }

        return 0;
    }

    private void unlock(List<Long> wallets) {
        wallets.sort(Comparator.reverseOrder());
        for (Long walletId : wallets) {
            if (lockedWallets.remove(walletId) == null) {
                throw new IllegalStateException("Trying to unlock not locked wallet: " + walletId);
            }
        }
    }

    protected Wallet getWallet(long walletId) {
        Wallet wallet = walletsById.get(walletId);

        if (wallet == null) {
            throw new WalletNotFoundException(walletId);
        }

        return wallet;
    }

    private Wallet makeWallet(long accountId, Coin coin) {
        Wallet wallet = new Wallet(accountId, makeWalletAddress(coin), coin);
        persist(wallet);
        walletsById.put(wallet.getId(), wallet);
        return wallet;
    }

    protected Wallet persist(Wallet wallet) {
        wallet.setId(ThreadLocalRandom.current().nextLong());
        return wallet;
    }

    protected String makeWalletAddress(Coin coin) {
        return UUID.randomUUID().toString();
    }
}

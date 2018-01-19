package code8.launcher.logic;

import code8.launcher.logic.changer.WalletServiceBase;
import code8.launcher.model.changer.Wallet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static code8.launcher.model.changer.Coin.BTC;
import static code8.launcher.model.changer.Coin.RUB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * todo: javadoc
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WalletServiceBaseTest {
    @Autowired
    private WalletServiceBase walletService;

    @Test
    public void walletCreationTest() {
        long accountId = 42L;
        Wallet walletBTC = walletService.getAccountCoinWallet(accountId, BTC);
        assertNotNull(walletBTC);
        assertNotNull(walletBTC.getId());
        assertEquals(accountId, walletBTC.getAccountId());
        assertEquals(BTC, walletBTC.getCoin());
        assertEquals(BigDecimal.ZERO, walletBTC.getBalance());
        assertEquals(BigDecimal.ZERO, walletService.getWalletBalance(walletBTC.getId()));

        Wallet walletRUB = walletService.getAccountCoinWallet(accountId, RUB);
        assertNotNull(walletBTC);
        assertNotNull(walletBTC.getId());
        assertNotEquals(walletBTC.getId(), walletRUB.getId());
    }
}

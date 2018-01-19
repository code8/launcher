package code8.launcher.logic;

import code8.launcher.logic.changer.OrderService;
import code8.launcher.logic.changer.WalletServiceBase;
import code8.launcher.model.changer.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static code8.launcher.model.changer.CoinPair.BTCvsUSD;
import static code8.launcher.model.changer.Order.Status.completed;
import static code8.launcher.model.changer.Order.Status.created;
import static code8.launcher.model.changer.OrderRequest.Type.Ask;
import static code8.launcher.model.changer.OrderRequest.Type.Bid;
import static java.math.BigDecimal.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletServiceBase walletService;

    class TestData {
        long bidAccountId = ThreadLocalRandom.current().nextLong();
        long askAccountId = ThreadLocalRandom.current().nextLong();
        BigDecimal bidFundsWalletInitialBalance, bidProductWalletInitialBalance;
        BigDecimal askFundsWalletInitialBalance, askProductWalletInitialBalance;

        Wallet bidProductWallet, bidFundsWallet;
        Wallet askFundsWallet, askProductWallet;
    }

    @Test
    public void testOrderProcessingBidOneToOneCompleted() {
        // limit order
        BigDecimal askAmount = TEN;
        BigDecimal askRate = ONE.add(ONE);

        // market order
        BigDecimal bidAmount = askAmount.multiply(askRate);
        BigDecimal bidRate = null;

        // initialize wallets
        OrderServiceTest.TestData data = prepareTestData(BTCvsUSD,
                askAmount.multiply(askRate), ZERO, // bid
                askAmount, ZERO);                  // ask


        Order askOrder = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);
        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);


        assertEquals(created, bidOrder.getStatus());
        assertEquals(created, askOrder.getStatus());

        orderService.processMarketOrders();

        assertEquals(completed, bidOrder.getStatus());
        assertEquals(completed, askOrder.getStatus());
        assertEquals(askAmount, data.bidProductWallet.getBalance());
        assertEquals(ZERO, data.bidFundsWallet.getBalance());
        assertEquals(ZERO, data.askFundsWallet.getBalance());
        assertEquals(askAmount.multiply(askRate), data.askProductWallet.getBalance());
    }

    @Test
    public void testOrderProcessingAskOneToOneCompleted() {
        // limit order
        BigDecimal bidAmount = TEN;
        BigDecimal bidRate = ONE.add(ONE);

        // market order
        BigDecimal askAmount = bidAmount;
        BigDecimal askRate = null;

        // initialize wallets
        OrderServiceTest.TestData data = prepareTestData(BTCvsUSD,
                bidAmount.multiply(bidRate), ZERO, // bid
                askAmount, ZERO);                  // ask


        Order askOrder = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);
        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);


        assertEquals(created, bidOrder.getStatus());
        assertEquals(created, askOrder.getStatus());

        orderService.processMarketOrders();

        assertEquals(completed, bidOrder.getStatus());
        assertEquals(completed, askOrder.getStatus());
        assertEquals(bidAmount, data.bidProductWallet.getBalance());
        assertEquals(ZERO, data.bidFundsWallet.getBalance());
        assertEquals(ZERO, data.askFundsWallet.getBalance());
        assertEquals(askAmount.multiply(bidRate), data.askProductWallet.getBalance());
    }

    @Test
    public void testOrderProcessingBidOneToManyCompleted() {
        BigDecimal askAmount1 = TEN;
        BigDecimal askAmount2 = TEN;
        BigDecimal askAmount3 = TEN;
        BigDecimal askRate1 = ONE;
        BigDecimal askRate2 = ONE.add(ONE);
        BigDecimal askRate3 = askRate1;

        BigDecimal bidAmount = askAmount1.multiply(askRate1).add(askAmount2.multiply(askRate2)).add(askAmount3.multiply(askRate3));
        BigDecimal bidRate = null;

        // initialize wallets
        OrderServiceTest.TestData data = prepareTestData(BTCvsUSD,
                bidAmount, ZERO, // bid
                askAmount1.add(askAmount2).add(askAmount3), ZERO);                                      // ask


        Order askOrder1 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount1, askRate1);
        Order askOrder2 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount2, askRate2);
        Order askOrder3 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount3, askRate3);
        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, data.bidFundsWallet.getBalance(), bidRate);


        assertEquals(created, bidOrder.getStatus());
        assertEquals(created, askOrder1.getStatus());
        assertEquals(created, askOrder2.getStatus());
        assertEquals(created, askOrder3.getStatus());

        orderService.processMarketOrders();

        assertEquals(completed, bidOrder.getStatus());
        assertEquals(completed, askOrder1.getStatus());
        assertEquals(completed, askOrder2.getStatus());
        assertEquals(completed, askOrder3.getStatus());
        assertEquals(askAmount1.add(askAmount2).add(askAmount3), data.bidProductWallet.getBalance());
        assertEquals(ZERO, data.bidFundsWallet.getBalance());
        assertEquals(ZERO, data.askFundsWallet.getBalance());
        assertEquals(bidAmount, data.askProductWallet.getBalance());
    }

    @Test
    public void testOrderProcessingAskOneToManyCompleted() {
        BigDecimal bidAmount1 = TEN;
        BigDecimal bidAmount2 = TEN;
        BigDecimal bidAmount3 = TEN;
        BigDecimal bidRate1 = ONE;
        BigDecimal bidRate2 = ONE.add(ONE);
        BigDecimal bidRate3 = bidRate1;

        BigDecimal askAmount = bidAmount1.add(bidAmount2).add(bidAmount3);
        BigDecimal bidRate = null;

        // initialize wallets
        OrderServiceTest.TestData data = prepareTestData(BTCvsUSD,
                bidAmount1.multiply(bidRate1).add(bidAmount2.multiply(bidRate2)).add(bidAmount3.multiply(bidRate3)), ZERO, // bid
                askAmount, ZERO);                                      // ask


        Order bidOrder1 = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount1, bidRate1);
        Order bidOrder2 = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount2, bidRate2);
        Order bidOrder3 = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount3, bidRate3);
        Order askOrder = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, bidRate);


        assertEquals(created, askOrder.getStatus());
        assertEquals(created, bidOrder1.getStatus());
        assertEquals(created, bidOrder2.getStatus());
        assertEquals(created, bidOrder3.getStatus());

        orderService.processMarketOrders();

        assertEquals(completed, askOrder.getStatus());
        assertEquals(completed, bidOrder1.getStatus());
        assertEquals(completed, bidOrder2.getStatus());
        assertEquals(completed, bidOrder3.getStatus());
        assertEquals(bidAmount1.add(bidAmount2).add(bidAmount3), data.bidProductWallet.getBalance());
        assertEquals(ZERO, data.bidFundsWallet.getBalance());
        assertEquals(ZERO, data.askFundsWallet.getBalance());
        assertEquals(bidAmount1.multiply(bidRate1).add(bidAmount2.multiply(bidRate2)).add(bidAmount3.multiply(bidRate3)), data.askProductWallet.getBalance());
    }

    private Wallet makeWallet(long accountId, Coin coin, BigDecimal balance) {
        Wallet wallet = walletService.getAccountCoinWallet(accountId, coin);
        wallet.setBalance(balance);
        return wallet;
    }

    private Order makeOrder(CoinPair pair, long accountId, OrderRequest.Type type, BigDecimal volume, BigDecimal rate) {
        OrderRequest bidRequest = new OrderRequest(ThreadLocalRandom.current().nextInt());
        bidRequest.setType(type);
        bidRequest.setPair(pair);
        bidRequest.setAccountId(accountId);
        bidRequest.setRate(rate);
        bidRequest.setVolume(volume);
        return orderService.makeOrder(bidRequest);
    }


    private OrderServiceTest.TestData prepareTestData(CoinPair pair, BigDecimal bidFundsWalletInitialBalance, BigDecimal bidProductWalletInitialBalance,
                                                            BigDecimal askFundsWalletInitialBalance, BigDecimal askProductWalletInitialBalance) {

        OrderServiceTest.TestData data = new OrderServiceTest.TestData();

        data.bidFundsWalletInitialBalance = bidFundsWalletInitialBalance;
        data.bidProductWalletInitialBalance = bidProductWalletInitialBalance;

        data.askFundsWalletInitialBalance = askFundsWalletInitialBalance;
        data.askProductWalletInitialBalance = askProductWalletInitialBalance;

        data.bidProductWallet = makeWallet(data.bidAccountId, pair.getBase(), bidProductWalletInitialBalance);
        data.bidFundsWallet = makeWallet(data.bidAccountId, pair.getCounter(), bidFundsWalletInitialBalance);

        data.askFundsWallet = makeWallet(data.askAccountId, pair.getBase(), askFundsWalletInitialBalance);
        data.askProductWallet = makeWallet(data.askAccountId, pair.getCounter(), askProductWalletInitialBalance);

        return data;
    }
}

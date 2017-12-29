package code8.launcher.logic;

import code8.launcher.logic.changer.OrderService;
import code8.launcher.logic.changer.TransactionService;
import code8.launcher.logic.changer.WalletServiceBase;
import code8.launcher.model.changer.Coin;
import code8.launcher.model.changer.CoinPair;
import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
import code8.launcher.model.changer.OrderTransaction;
import code8.launcher.model.changer.Wallet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static code8.launcher.model.changer.CoinPair.BTCvsUSD;
import static code8.launcher.model.changer.Order.Status.completed;
import static code8.launcher.model.changer.Order.Status.processing;
import static code8.launcher.model.changer.OrderRequest.Type.Ask;
import static code8.launcher.model.changer.OrderRequest.Type.Bid;
import static org.junit.Assert.assertEquals;

/**
 * todo: javadoc
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private WalletServiceBase walletService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TransactionService transactionService;

    class TestData {
        long bidAccountId = ThreadLocalRandom.current().nextLong();
        long askAccountId = ThreadLocalRandom.current().nextLong();
        BigDecimal bidFundsWalletInitialBalance, bidProductWalletInitialBalance;
        BigDecimal askFundsWalletInitialBalance, askProductWalletInitialBalance;

        Wallet bidProductWallet, bidFundsWallet;
        Wallet askFundsWallet, askProductWallet;
    }


    @Test
    public void handleTransactionLimitLimitFull() {
        TestData data = prepareTestData(new BigDecimal(20000), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, BTCvsUSD);

        BigDecimal bidAmount = BigDecimal.TEN;
        BigDecimal bidRate = new BigDecimal(2000);


        BigDecimal askAmount = BigDecimal.TEN;
        BigDecimal askRate = new BigDecimal(1000);

        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);
        Order askOrder = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);

        OrderTransaction transaction = new OrderTransaction(bidOrder.getId(), askOrder.getId());

        transactionService.handleTransaction(transaction);

        assertEquals(data.bidFundsWalletInitialBalance.subtract(bidAmount.multiply(bidRate)), data.bidFundsWallet.getBalance());
        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount), data.askFundsWallet.getBalance());
        assertEquals(data.bidProductWalletInitialBalance.add(bidAmount), data.bidProductWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate)), data.askProductWallet.getBalance());

        assertEquals(completed, askOrder.getStatus());
        assertEquals(completed, bidOrder.getStatus());

        if (bidRate.compareTo(askRate) > 0) {
            // check unused funds
            assertEquals(bidRate.subtract(askRate).multiply(bidAmount), bidOrder.getCurrentFunds());
        }

    }

    @Test
    public void handleTransactionLimitLimitPartially() {
        TestData data = prepareTestData(new BigDecimal(20000), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, BTCvsUSD);

        BigDecimal bidAmount = BigDecimal.TEN;
        BigDecimal bidRate = new BigDecimal(2000);


        BigDecimal askAmount = new BigDecimal(5);
        BigDecimal askRate = new BigDecimal(1000);

        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);
        Order askOrder1 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);

        transactionService.handleTransaction(new OrderTransaction(bidOrder.getId(), askOrder1.getId()));

        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount), data.askFundsWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate)), data.askProductWallet.getBalance());

        assertEquals(completed, askOrder1.getStatus());
        assertEquals(processing, bidOrder.getStatus());

        Order askOrder2 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);
        transactionService.handleTransaction(new OrderTransaction(bidOrder.getId(), askOrder2.getId()));

        assertEquals(data.bidFundsWalletInitialBalance.subtract(bidAmount.multiply(bidRate)), data.bidFundsWallet.getBalance());
        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount.multiply(new BigDecimal(2))), data.askFundsWallet.getBalance());
        assertEquals(data.bidProductWalletInitialBalance.add(bidAmount), data.bidProductWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate).multiply(new BigDecimal(2))), data.askProductWallet.getBalance());

        assertEquals(completed, askOrder2.getStatus());
        assertEquals(completed, bidOrder.getStatus());

        if (bidRate.compareTo(askRate) > 0) {
            // check unused funds
            assertEquals(bidRate.subtract(askRate).multiply(bidAmount), bidOrder.getCurrentFunds());
        }
    }

    @Test
    public void handleTransactionMarketLimitFull() {
        TestData data = prepareTestData(new BigDecimal(20000), BigDecimal.ZERO, BigDecimal.TEN, new BigDecimal(3000), BTCvsUSD);

        BigDecimal bidAmount = new BigDecimal(5000);
        BigDecimal bidRate = null;


        BigDecimal askAmount = new BigDecimal(5);
        BigDecimal askRate = new BigDecimal(1000);

        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);
        Order askOrder = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);

        transactionService.handleTransaction(new OrderTransaction(bidOrder.getId(), askOrder.getId()));

        assertEquals(data.bidFundsWalletInitialBalance.subtract(bidAmount), data.bidFundsWallet.getBalance());
        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount), data.askFundsWallet.getBalance());
        assertEquals(bidAmount.divide(askRate), data.bidProductWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate)), data.askProductWallet.getBalance());
        assertEquals(BigDecimal.ZERO, bidOrder.getCurrentFunds());

        assertEquals(completed, askOrder.getStatus());
        assertEquals(completed, bidOrder.getStatus());
    }

    @Test
    public void handleTransactionMarketLimitPartially() {
        TestData data = prepareTestData(new BigDecimal(20000), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ZERO, BTCvsUSD);

        BigDecimal bidAmount = new BigDecimal(10000);
        BigDecimal bidRate = null;


        BigDecimal askAmount = new BigDecimal(5);
        BigDecimal askRate = new BigDecimal(1000);

        Order bidOrder = makeOrder(BTCvsUSD, data.bidAccountId, Bid, bidAmount, bidRate);
        Order askOrder1 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);

        transactionService.handleTransaction(new OrderTransaction(bidOrder.getId(), askOrder1.getId()));

        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount), data.askFundsWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate)), data.askProductWallet.getBalance());

        assertEquals(completed, askOrder1.getStatus());
        assertEquals(processing, bidOrder.getStatus());

        Order askOrder2 = makeOrder(BTCvsUSD, data.askAccountId, Ask, askAmount, askRate);
        transactionService.handleTransaction(new OrderTransaction(bidOrder.getId(), askOrder2.getId()));

        assertEquals(data.bidFundsWalletInitialBalance.subtract(bidAmount), data.bidFundsWallet.getBalance());
        assertEquals(data.askFundsWalletInitialBalance.subtract(askAmount.multiply(new BigDecimal(2))), data.askFundsWallet.getBalance());
        assertEquals(bidAmount.divide(askRate), data.bidProductWallet.getBalance());
        assertEquals(data.askProductWalletInitialBalance.add(askAmount.multiply(askRate).multiply(new BigDecimal(2))), data.askProductWallet.getBalance());
        assertEquals(BigDecimal.ZERO, bidOrder.getCurrentFunds());

        assertEquals(completed, askOrder2.getStatus());
        assertEquals(completed, bidOrder.getStatus());
    }


    private Wallet makeWallet(long accountId, Coin coin, BigDecimal balance) {
        Wallet wallet = walletService.getWallet(accountId, coin);
        wallet.setBalance(balance);
        return wallet;
    }

    private Order makeOrder(CoinPair pair, long accountId, OrderRequest.Type type, BigDecimal volume, BigDecimal rate) {
        OrderRequest bidRequest = new OrderRequest();
        bidRequest.setType(type);
        bidRequest.setPair(pair);
        bidRequest.setAccountId(accountId);
        bidRequest.setRate(rate);
        bidRequest.setVolume(volume);
        return orderService.makeOrder(bidRequest);
    }

    private TestData prepareTestData(BigDecimal bidFundsWalletInitialBalance, BigDecimal bidProductWalletInitialBalance,
                                     BigDecimal askFundsWalletInitialBalance, BigDecimal askProductWalletInitialBalance, CoinPair pair) {

        TestData data = new TestData();

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

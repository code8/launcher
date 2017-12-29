package code8.launcher.logic.changer;

import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderTransaction;
import code8.launcher.model.changer.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * todo: javadoc
 */
@Service
public class TransactionServiceBase implements TransactionService {
    protected ConcurrentMap<Long, Wallet> lockedWallets = new ConcurrentHashMap<>();

    @Autowired
    protected WalletService walletService;

    @Autowired
    protected OrderService orderService;

    @Override
    public void handleTransaction(OrderTransaction transaction) {
        transaction.setStatus(OrderTransaction.Status.processing);

        Order bid = orderService.getOrder(transaction.getBidId());
        Order ask = orderService.getOrder(transaction.getAskId());

        bid.setStatus(Order.Status.processing);
        ask.setStatus(Order.Status.processing);

        BigDecimal actualRate, transferAmount, productToAdd;

        if (!bid.isLimitOrder() && !ask.isLimitOrder()) {
            // two market orders
            throw new IllegalStateException("Two market orders in transaction");
        }

        if (bid.isLimitOrder() && ask.isLimitOrder()) {
            if (ask.getRate().compareTo(bid.getRate()) > 0) {
                throw new IllegalStateException("Limit orders rate not match");
            }

            actualRate = ask.getRate();
            // delta = bid.getRate() - ask.getRate()

        } else {
            actualRate = bid.isLimitOrder() ? bid.getRate() : ask.getRate();
        }

        transferAmount = bid.getCurrentFunds().min(ask.getCurrentFunds().multiply(actualRate));
        productToAdd = transferAmount.divide(actualRate);

        walletService.changeBalance(bid.getProductWalletId(), productToAdd);
        walletService.changeBalance(ask.getProductWalletId(), transferAmount);

        bid.spendFunds(transferAmount);
        bid.addProduct(productToAdd);

        ask.spendFunds(transferAmount.divide(actualRate));
        ask.addProduct(transferAmount);

        transaction.setTransferAmount(transferAmount);
        transaction.setActualRate(actualRate);
        transaction.setStatus(OrderTransaction.Status.processed);

        if (ask.isComplete()) {
            ask.setStatus(Order.Status.completed);
        }

        if (bid.isComplete()) {
            bid.setStatus(Order.Status.completed);
            if (bid.getCurrentFunds().compareTo((BigDecimal.ZERO)) > 0) {
                // handle unused funds
            }
        }
    }
}

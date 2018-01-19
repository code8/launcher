package code8.launcher.logic;

import code8.launcher.model.Order;
import code8.launcher.model.OrderTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static code8.launcher.model.Order.Status.completed;
import static code8.launcher.model.Order.Status.processing;
import static code8.launcher.model.OrderTransaction.Status.processed;


/**
 * todo: javadoc
 */
@Service
public class TransactionServiceBase implements TransactionService {

    protected final WalletService walletService;

    @Autowired
    public TransactionServiceBase(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void handleTransaction(OrderTransaction transaction) {
        transaction.setStatus(OrderTransaction.Status.processing);

        Order bid = transaction.getBidOrder();
        Order ask = transaction.getAskOrder();

        bid.setStatus(processing);
        ask.setStatus(processing);

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

        walletService.changeBalance(Stream.of(
                new WalletService.WalletBalanceChange(bid.getProductWalletId(), productToAdd),
                new WalletService.WalletBalanceChange(ask.getProductWalletId(), transferAmount)
                ).collect(Collectors.toList())
        );

        bid.spendFunds(transferAmount);
        bid.addProduct(productToAdd);

        ask.spendFunds(transferAmount.divide(actualRate));
        ask.addProduct(transferAmount);

        transaction.setTransferAmount(transferAmount);
        transaction.setActualRate(actualRate);
        transaction.setStatus(processed);

        if (ask.isComplete()) {
            ask.setStatus(completed);
        }

        if (bid.isComplete()) {
            bid.setStatus(completed);
            if (bid.getCurrentFunds().compareTo((BigDecimal.ZERO)) > 0) {
                // handle unused funds
            }
        }
    }
}

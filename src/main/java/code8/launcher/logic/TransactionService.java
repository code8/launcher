package code8.launcher.logic;

import code8.launcher.model.OrderTransaction;

/**
 * todo: javadoc
 */
public interface TransactionService {
    void handleTransaction(OrderTransaction transaction);
}

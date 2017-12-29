package code8.launcher.logic.changer;

import code8.launcher.model.changer.OrderTransaction;

/**
 * todo: javadoc
 */
public interface TransactionService {
    void handleTransaction(OrderTransaction transaction);
}

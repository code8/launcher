package code8.launcher.logic;

import code8.launcher.model.Order;
import code8.launcher.model.OrderRequest;

/**
 * todo: javadoc
 */
public interface OrderService {
    Order makeOrder(OrderRequest request);
    Order getOrder(long id);
    void cancelOrder(long id);
    void processMarketOrders();

    class CancelMargetOrderException extends RuntimeException {
        public CancelMargetOrderException(long orderId) {
            super("Market order: " + orderId + " cannot been canceled");
        }
    }

    class CancelLimitOrderException extends RuntimeException {
        public CancelLimitOrderException(long orderId, Order.Status status) {
            super("Limit order: " + orderId + " in status: " + status + " cannot been canceled");
        }
    }
}

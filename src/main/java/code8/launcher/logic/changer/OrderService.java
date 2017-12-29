package code8.launcher.logic.changer;

import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;

/**
 * todo: javadoc
 */
public interface OrderService {
    Order makeOrder(OrderRequest request);
    Order getOrder(long id);
    void cancelOrder(long id);
}

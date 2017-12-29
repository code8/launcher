package code8.launcher.logic.changer;

import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
import code8.launcher.model.changer.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * todo: javadoc
 */
@Service
public class OrderServiceBase implements OrderService {
    ConcurrentMap<Long, Order> orders = new ConcurrentHashMap<>();

    @Autowired
    protected WalletService walletService;

    @Override
    public Order getOrder(long id) {
        return orders.get(id);
    }

    @Override
    public void cancelOrder(long id) {
        Order order = orders.get(id);
        makeReserveOnWallet(order, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Order makeOrder(OrderRequest request) {
        Order order = Order.fromRequest(request);
        makeReserveOnWallet(order, false);

        try {
            Wallet fundsWallet = walletService.getWallet(request.getAccountId(), order.getFundsCoin());
            Wallet productWallet = walletService.getWallet(request.getAccountId(), order.getProductCoin());
            order.setFundsWalletId(fundsWallet.getId());
            order.setProductWalletId(productWallet.getId());
            persist(order);
            orders.put(order.getId(), order);

        } catch (Exception e) {
            makeReserveOnWallet(order, true);
            throw e;
        }

        return order;
    }

    private Order persist(Order order) {
        order.setId(ThreadLocalRandom.current().nextLong());
        return order;
    }

    private void makeReserveOnWallet(Order order, boolean cancel) {
        Wallet reservationWallet = walletService.getWallet(order.getAccountId(), order.getFundsCoin());
        BigDecimal amount = cancel ? order.getCurrentFunds() : order.getCurrentFunds().negate();
        walletService.changeBalance(reservationWallet.getId(), amount);
    }
}

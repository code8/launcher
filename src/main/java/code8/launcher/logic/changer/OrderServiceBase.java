package code8.launcher.logic.changer;

import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
import code8.launcher.model.changer.OrderTransaction;
import code8.launcher.model.changer.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

import static code8.launcher.model.changer.Order.Status.*;
import static code8.launcher.model.changer.OrderRequest.Type.Bid;

/**
 * todo: javadoc
 */
@Service
public class OrderServiceBase implements OrderService {
    private final static int INITIAL_CAPACITY = 42;

    protected final ConcurrentMap<Long, Order> orders = new ConcurrentHashMap<>(INITIAL_CAPACITY);
    protected final ConcurrentNavigableMap<BigDecimal, Set<Long>> bidOrders = new ConcurrentSkipListMap<>();
    protected final ConcurrentNavigableMap<BigDecimal, Set<Long>> askOrders = new ConcurrentSkipListMap<>();
    protected final PriorityBlockingQueue<Order> marketOrders = new PriorityBlockingQueue<>(INITIAL_CAPACITY,Comparator.comparing(Order::getPriority));

    protected final WalletService walletService;
    protected final TransactionService transactionService;

    @Autowired
    public OrderServiceBase(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public Order getOrder(long id) {
        return orders.get(id);
    }

    @Override
    public void cancelOrder(long id) {
        Order order = orders.get(id);
        if (order.isLimitOrder()) {
            if (order.getStatus() == created) {
                makeReserveOnWallet(order, true);
                removeOrder(order);
                order.setStatus(canceled);
                persist(order);
            } else {
                throw new CancelLimitOrderException(order.getId(), order.getStatus());
            }
        } else {
            throw new CancelMargetOrderException(order.getId());
        }
    }

    @Override
    public void processMarketOrders() {
        Order topMarketOrder = marketOrders.poll();
        if (topMarketOrder != null) {
            ConcurrentNavigableMap<BigDecimal, Set<Long>> priceMap = topMarketOrder.getType() == Bid ? askOrders : bidOrders.descendingMap();
            done:
            for (Map.Entry<BigDecimal, Set<Long>> entry : priceMap.entrySet()) {
                Iterator<Long> appositeOrders = entry.getValue().iterator();
                while (appositeOrders.hasNext()) {
                    Long limitOrderId = appositeOrders.next();
                    Order limitOrder = getOrder(limitOrderId);

                    Order bidOrder = topMarketOrder.getType() == Bid ? topMarketOrder : limitOrder;
                    Order askOrder = bidOrder == topMarketOrder ? limitOrder : topMarketOrder;

                    OrderTransaction transaction = new OrderTransaction(bidOrder, askOrder);
                    transactionService.handleTransaction(transaction);

                    if (limitOrder.getStatus() == completed) {
                        appositeOrders.remove();
                        removeOrder(limitOrder);
                        persist(limitOrder);
                    }

                    if (topMarketOrder.getStatus() == completed) {
                        persist(topMarketOrder);
                        orders.remove(topMarketOrder.getId());
                        break done;
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Order makeOrder(OrderRequest request) {
        Order order = Order.fromRequest(request);
        makeReserveOnWallet(order, false);

        try {
            Wallet fundsWallet = walletService.getAccountCoinWallet(request.getAccountId(), order.getFundsCoin());
            Wallet productWallet = walletService.getAccountCoinWallet(request.getAccountId(), order.getProductCoin());
            order.setFundsWalletId(fundsWallet.getId());
            order.setProductWalletId(productWallet.getId());
            persist(order);

            orders.put(order.getId(), order);


            if (order.isLimitOrder()) {
                // add limit order to price list
                ConcurrentMap<BigDecimal, Set<Long>> priceMap = order.getType() == Bid ? bidOrders : askOrders;
                priceMap.computeIfAbsent(order.getRate(), price -> new HashSet<>()).add(order.getId());
            } else {
                // add market order to wait list
                marketOrders.add(order);
            }

        } catch (Exception e) {
            makeReserveOnWallet(order, true);
            throw e;
        }

        return order;
    }

    private void removeOrder(Order order) {
        orders.remove(order.getId());
        ConcurrentMap<BigDecimal, Set<Long>> priceMap = order.getType() == Bid ? bidOrders : askOrders;
        priceMap.computeIfPresent(order.getRate(), (price, list) -> {
            list.remove(order.getId());
            return list.isEmpty() ? null : list;
        });
    }

    private Order persist(Order order) {
        if (order.getId() == 0) {
            order.setId(ThreadLocalRandom.current().nextLong());
        }

        return order;
    }

    private void makeReserveOnWallet(Order order, boolean cancel) {
        Wallet reservationWallet = walletService.getAccountCoinWallet(order.getAccountId(), order.getFundsCoin());
        BigDecimal amount = cancel ? order.getCurrentFunds() : order.getCurrentFunds().negate();
        walletService.changeBalance(Collections.singletonList(new WalletService.WalletBalanceChange(reservationWallet.getId(), amount)));
    }
}

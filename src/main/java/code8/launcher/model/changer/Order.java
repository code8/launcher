package code8.launcher.model.changer;

import code8.launcher.model.changer.OrderRequest.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

import static code8.launcher.model.changer.Order.Status.created;

/**
 * todo: javadoc
 */
public abstract class Order {
    public enum Status {
        created, processing, completed, error, canceled
    }

    public static Order fromRequest(OrderRequest request) {
        return request.getType() == Type.Bid ? new BidOrder(request) : new AskOrder(request);
    }

    protected Order(OrderRequest request) {
        this.accountId = request.accountId;
        this.type = request.type;
        this.pair = request.pair;
        this.rate = request.rate;
    }

    protected final Type type;
    protected final CoinPair pair;
    protected final BigDecimal rate;
    protected BigDecimal currentProduct = BigDecimal.ZERO;

    private long fundsWalletId;
    private long productWalletId;

    // processing attributes
    private long id;
    private final long accountId;
    private int priority = 0;
    private Status status = created;
    private long timestamp = System.currentTimeMillis();
    private int workerId;

    @JsonIgnore
    public abstract Coin getFundsCoin();
    @JsonIgnore
    public abstract Coin getProductCoin();
    @JsonIgnore
    public abstract BigDecimal getCurrentFunds();
    @JsonIgnore
    public abstract void spendFunds(BigDecimal transferredFunds);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public long getFundsWalletId() {
        return fundsWalletId;
    }

    public void setFundsWalletId(long fundsWalletId) {
        this.fundsWalletId = fundsWalletId;
    }

    public long getProductWalletId() {
        return productWalletId;
    }

    public void setProductWalletId(long productWalletId) {
        this.productWalletId = productWalletId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public Type geType() {
        return type;
    }

    @JsonIgnore
    public boolean isLimitOrder() {
        return rate != null;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public long getAccountId() {
        return accountId;
    }

    public CoinPair getPair() {
        return pair;
    }

    public BigDecimal getCurrentProduct() {
        return currentProduct;
    }

    public void addProduct(BigDecimal product) {
        currentProduct = currentProduct.add(product);
    }

    @JsonIgnore
    public abstract boolean isComplete();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return  getId() == order.getId() &&
                getAccountId() == order.getAccountId() &&
                getTimestamp() == order.getTimestamp() &&
                getType() == order.getType() &&
                getPair() == order.getPair() &&
                getCurrentProduct().equals(order.getCurrentProduct()) &&
                getStatus() == order.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

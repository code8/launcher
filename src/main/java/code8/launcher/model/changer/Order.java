package code8.launcher.model.changer;

import code8.launcher.model.changer.OrderRequest.Type;

import java.math.BigDecimal;

import static code8.launcher.model.changer.Order.Status.created;

/**
 * todo: javadoc
 */
public abstract class Order {
    public enum Status {
        created, processing, completed, error;
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

    public abstract Coin getFundsCoin();
    public abstract Coin getProductCoin();
    public abstract BigDecimal getCurrentFunds();
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

    public void addProduct(BigDecimal product) {
        currentProduct = currentProduct.add(product);
    }

    public abstract boolean isComplete();
}

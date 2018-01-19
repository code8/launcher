package code8.launcher.model;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class BidOrder extends Order {
    private final BigDecimal initialFunds;
    private BigDecimal currentFunds;
    private BigDecimal productTarget;

    public BidOrder() {
        super(new OrderRequest(0));
        initialFunds = BigDecimal.ZERO;
    }

    public BidOrder(OrderRequest request) {
        super(request);
        this.initialFunds = isLimitOrder() ? request.volume.multiply(rate) : request.volume;
        productTarget = isLimitOrder() ? request.volume : BigDecimal.ZERO;
        currentFunds = initialFunds;
        currentProduct = BigDecimal.ZERO;
    }

    @Override
    public Coin getFundsCoin() {
        return pair.getCounter();
    }

    @Override
    public Coin getProductCoin() {
        return pair.getBase();
    }

    @Override
    public BigDecimal getCurrentFunds() {
        return currentFunds;
    }

    @Override
    public void spendFunds(BigDecimal transferredFunds) {
        currentFunds = currentFunds.subtract(transferredFunds);
    }

    @Override
    public boolean isComplete() {
        if (isLimitOrder()) {
            return productTarget.compareTo(currentProduct) == 0;

        } else {
            return BigDecimal.ZERO.equals(currentFunds);
        }
    }
}

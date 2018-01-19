package code8.launcher.model;

import java.math.BigDecimal;

import static code8.launcher.model.OrderRequest.Type.Bid;

/**
 * todo: javadoc
 */
public class AskOrder extends Order {
    private final BigDecimal initialFunds;
    private BigDecimal currentFunds;

    public AskOrder(OrderRequest request) {
        super(request);
        this.initialFunds = isLimitOrder() && type == Bid ? request.volume.multiply(rate) : request.volume;
        currentFunds = initialFunds;
    }

    public Coin getFundsCoin() {
        return pair.getBase();
    }

    public Coin getProductCoin() {
        return pair.getCounter();
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
        return BigDecimal.ZERO.equals(currentFunds);
    }
}

package code8.launcher.model;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class OrderTransaction {

    public enum Status {
        created, processing, processed, error
    }

    long id;
    Status status = Status.created;
    private final Order bidOrder;
    private final Order askOrder;
    private BigDecimal transferAmount;
    private BigDecimal actualRate;
    long timestamp = System.currentTimeMillis();
    int workerId;

    public OrderTransaction(Order bidOrder, Order askOrder) {
        this.bidOrder = bidOrder;
        this.askOrder = askOrder;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getBidOrder() {
        return bidOrder;
    }

    public Order getAskOrder() {
        return askOrder;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getActualRate() {
        return actualRate;
    }

    public void setActualRate(BigDecimal actualRate) {
        this.actualRate = actualRate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }
}

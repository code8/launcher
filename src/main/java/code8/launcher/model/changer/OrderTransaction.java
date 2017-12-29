package code8.launcher.model.changer;

import java.math.BigDecimal;

/**
 * todo: javadoc
 */
public class OrderTransaction {

    public enum Status {
        created, processing, processed, error;
    }

    long id;
    Status status = Status.created;
    private final long bidId;
    private final long askId;
    private BigDecimal transferAmount;
    private BigDecimal actualRate;
    long timestamp = System.currentTimeMillis();
    int workerId;

    public OrderTransaction(long bidId, long askId) {
        this.bidId = bidId;
        this.askId = askId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBidId() {
        return bidId;
    }

    public long getAskId() {
        return askId;
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

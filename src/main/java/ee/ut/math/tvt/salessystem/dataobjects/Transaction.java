package ee.ut.math.tvt.salessystem.dataobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Transaction {
    private LocalDate localDate;
    private LocalTime localTime;
    private Long totalQuantity;
    private List<Purchase> purchases;

    public Transaction(LocalDate localDate, LocalTime localTime, Long totalQuantity, List<Purchase> purchases) {
        this.localDate = localDate;
        this.localTime = localTime;
        this.totalQuantity = totalQuantity;
        this.purchases = purchases;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    @Override
    public String toString() {
        return "Transaction: " +
                "date " + localDate +
                ", time " + localTime +
                ", quantity " + totalQuantity +
                ", purchases: " + purchases +
                '}';
    }
}
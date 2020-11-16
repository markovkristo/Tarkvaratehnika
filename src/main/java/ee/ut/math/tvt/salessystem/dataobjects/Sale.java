package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dateOfTransaction")
    private LocalDate dateOfTransaction;

    @Column(name = "timeOfTransaction")
    private LocalTime timeOfTransaction;

    @Column(name = "totalQuantity")
    private Long totalQuantity;

    @Transient
    private List<Purchase> purchases;

    public Sale(Long totalQuantity, List<Purchase> purchases) {
        this.dateOfTransaction = LocalDate.now();
        this.timeOfTransaction = LocalTime.now();
        this.totalQuantity = totalQuantity;
        this.purchases = purchases;
    }

    public LocalDate getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(LocalDate dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public LocalTime getTimeOfTransaction() {
        return timeOfTransaction;
    }

    public void setTimeOfTransaction(LocalTime timeOfTransaction) {
        this.timeOfTransaction = timeOfTransaction;
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
                "date " + dateOfTransaction +
                ", time " + timeOfTransaction +
                ", quantity " + totalQuantity +
                ", purchases: " + purchases +
                '}';
    }
}
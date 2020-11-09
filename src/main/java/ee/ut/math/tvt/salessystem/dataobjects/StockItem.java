package ee.ut.math.tvt.salessystem.dataobjects;


import javax.persistence.*;

/**
 * Stock item.
 */
@Entity
@Table(name = "stockitem")
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    @Transient
    private double price;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    @Transient
    private int quantity;
    


    public StockItem(Long id, String name, String desc, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void lowerQuantity(int quantity) {
        this.quantity -= quantity;
    }

    @Override
    public String toString() {
        return String.format("StockItem{id=%d, name='%s', quantity=%d, price=%f, description='%s'}", id, name, quantity, price, description);
    }
}

package ee.ut.math.tvt.salessystem.dataobjects;

import java.math.BigDecimal;

public class Purchase {
    private long productId;
    private String productName;
    private double productPrice;
    private int quantity;
    private BigDecimal sum;

    public Purchase(long productId, String productName, double productPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.sum = BigDecimal.valueOf(productPrice).multiply(BigDecimal.valueOf(quantity));
    }

    public long getProductId() {
        return productId;
    }

    public BigDecimal getSum() { return sum; }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                ", sum=" + sum +
                '}';
    }
}

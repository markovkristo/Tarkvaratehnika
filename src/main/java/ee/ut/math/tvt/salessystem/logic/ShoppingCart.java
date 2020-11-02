package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);
    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        if (itemIsInCart(item)) {
            items
                    .stream()
                    .filter(i -> i.getName().equals(item.getName()))
                    .findFirst()
                    .get()
                    .addMoreQuantity(item.getQuantity());
        } else {
            items.add(item);
        }
        log.debug("Added " + item.getName() + " quantity of ");
    }

    public void removeItem(SoldItem item, int amount) {
        for (int i = 0; i < items.size(); i++) {
            if (item.getName().equals(items.get(i).getName())) {
                int cartAmount = items.get(i).getQuantity();
                int newAmount = cartAmount - amount;
                if (newAmount > 0) {
                    items.get(i).setQuantity(newAmount);
                    log.info("Removed " + amount + " of " + item.getName() + " from shopping cart.");
                }else if( newAmount == 0){
                    items.remove(items.get(i));
                    log.info("Removed " + amount + " of " + item.getName() + " from shopping cart.");
                }else
                    System.out.println("Removable amount exeeds the items quantity in the cart, removable amount: " + amount + ", items quantity in cart: " + cartAmount);
                break;
            }
        }
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchaseGUI() {

    }

    public void submitCurrentPurchaseCLI() {
        System.out.println("Are you sure that you want to submit current purchase? (Yes/No)");
        Scanner choice = new Scanner(System.in);
        String input = choice.nextLine().toLowerCase();
        List<Purchase> purchases = new ArrayList<>();
        if (input.equals("yes")) {
            Transaction transaction = dao.beginTransaction();
            int allAmount = 0;
            try {
                List<StockItem> stockItems = dao.findStockItems();
                for (SoldItem item : items) {
                    dao.saveSoldItem(item);
                    String name = item.getName();
                    long idx = 0;
                    for (int i = 0; i < stockItems.size(); i++) {
                        if (name.equals(stockItems.get(i).getName()))
                            idx = stockItems.get(i).getId();
                    }
                    StockItem stockItem = dao.findStockItem(idx);
                    int soldAmount = item.getQuantity();
                    allAmount += soldAmount;
                    int amount = stockItem.getQuantity();
                    int newAmount = amount - soldAmount;
                    if (newAmount < 0) {
                        log.info("Removable amount can't exceed item quantity.");
                    } else if (newAmount == 0) {
                        stockItems.remove(stockItem);
                        log.info("All of the product (id: " + idx + ") has been removed from the warehouse.");
                    } else {
                        stockItem.setQuantity(newAmount);
                        log.info(soldAmount + " units of the product (id: " + idx + ") was removed from the warehouse");
                        Purchase purchase = new Purchase(idx, name, stockItem.getPrice(), soldAmount);
                        purchases.add(purchase);
                    }
                }
                transaction.setPurchases(purchases);
                transaction.setTotalQuantity(BigDecimal.valueOf(allAmount));
                dao.commitTransaction();
                items.clear();
            } catch (Exception e) {
                dao.rollbackTransaction();
                throw e;
            }
            log.info("Purchase is completed");
        } else {
            System.out.println("Didn't commit current purchase.");
        }
    }

    private boolean itemIsInCart(SoldItem item) {
        return items.stream().anyMatch(e -> e.getName().equals(item.getName()));
    }

    private boolean isItemInStock(SoldItem item) {
        return item.getQuantity() <= item.getStockItem().getQuantity();
    }
}

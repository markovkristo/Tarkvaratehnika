package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public void removeItem(SoldItem item) {
        items.remove(item);
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        System.out.println("Are you sure that you want to submit current purchase? (Yes/No)");
        Scanner choice = new Scanner(System.in);
        String input = choice.nextLine().toLowerCase();
        if(input.equals("yes")){
            dao.beginTransaction();
            try {
                List<StockItem> stockItems = dao.findStockItems();
                for (SoldItem item : items) {
                    dao.saveSoldItem(item);
                    String name = item.getName();
                    long idx = 0;
                    for (int i = 0; i < stockItems.size(); i++) {
                        if(name.equals(stockItems.get(i).getName()))
                            idx = stockItems.get(i).getId();
                    }
                    StockItem stockItem = dao.findStockItem(idx);
                    int soldAmount = item.getQuantity();
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
                    }
                }
                dao.commitTransaction();
                items.clear();
            } catch (Exception e) {
                dao.rollbackTransaction();
                throw e;
            }
            log.info("Purchase is completed");
        }else{
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

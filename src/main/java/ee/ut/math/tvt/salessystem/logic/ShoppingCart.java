package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
        log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
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
        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
            }
            dao.commitTransaction();
            items.clear();
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
        log.info("Purchase is completed");
    }

    private boolean itemIsInCart(SoldItem item) {
        return items.stream().anyMatch(e -> e.getName().equals(item.getName()));
    }

    private boolean isItemInStock(SoldItem item) {
        return item.getQuantity() <= item.getStockItem().getQuantity();
    }
}

package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Purchase;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);
    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private Warehouse warehouse;

    public ShoppingCart(SalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        if (quantityIsNegativeOrZero(item.getQuantity())) {
            throw new SalesSystemException("Product's quantity can't be zero or negative.");
        }
        if (!quantityOfItemCanBeAdded(item) && !itemIsInCart(item)) {
            throw new SalesSystemException("Desired quantity of " + item.getQuantity() + " exceeds the maximum quantity " +
            "of " + dao.findStockItem(item.getId()).getQuantity());
        }
        if (itemIsInCart(item)) {
            if (moreOfTheItemCanBeAdded(item)) {
                items
                        .stream()
                        .filter(i -> i.getName().equals(item.getName()))
                        .findFirst()
                        .get()
                        .addMoreQuantity(item.getQuantity());
            } else {
                int remainingQuantity = dao.findStockItem(item.getId()).getQuantity()
                        - items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().get().getQuantity();
                throw new SalesSystemException("Can't add " + item.getQuantity() + " of " + item.getName() + " to the cart.\nThis " +
                        "exceeds the remaining quantity of " + remainingQuantity + ".");
            }
        } else {
            items.add(item);
        }
        log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public void removeItemGUI(long id, int quantity) {
        if (quantityIsNegativeOrZero(quantity)) {
            throw new SalesSystemException("Removable quantity can't be zero or negative.");
        }
        if (items.stream().noneMatch(i -> i.getId() == id)) {
            throw new SalesSystemException("There aren't any items with ID of " + id + " in the cart.");
        }
        SoldItem item = items.stream().filter(i -> i.getId() == id).findFirst().get();
        if (item.getQuantity() < quantity) {
            String name = item.getName();
            items.removeIf(i -> i.getId() == id);
            throw new SalesSystemException("There aren't that many units of " + name + " in the cart. " +
                    "All of the product was removed from the cart.");
        }
        items.stream().filter(i -> i.getId() == id).findFirst().get().lowerQuantity(quantity);
        if (items.stream().filter(i -> i.getId() == id).findFirst().get().getQuantity() == 0) {
            items.removeIf(i -> i.getId() == id);
        }
    }

    public void removeItem(SoldItem item, int amount) {
        if (items.isEmpty())
            throw new SalesSystemException("Shopping cart is empty. ");
        else {
            for (int i = 0; i < items.size(); i++) {
                if (item.getName().equals(items.get(i).getName())) {
                    int cartAmount = items.get(i).getQuantity();
                    int newAmount = cartAmount - amount;
                    if (newAmount > 0) {
                        items.get(i).setQuantity(newAmount);
                        log.info("Removed " + amount + " of " + item.getName() + " from shopping cart.");
                    } else if (newAmount == 0) {
                        items.remove(items.get(i));
                        log.info("Removed " + amount + " of " + item.getName() + " from shopping cart.");
                    } else
                        throw new SalesSystemException("Removable amount exceeds the items quantity in the cart, removable amount: " + amount + ", items quantity in cart: " + cartAmount);
                    break;
                }
            }
        }
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        List<Purchase> purchases = new ArrayList<>();
        for (SoldItem item : items) {
            purchases.add(new Purchase(item.getId(), item.getName(), item.getPrice(), item.getQuantity()));
            warehouse.removeItemFromWarehouse(item.getId(), item.getQuantity(), dao);
            dao.saveSoldItem(item);
        }
        Sale sale = new Sale(items.stream().mapToLong(SoldItem::getQuantity).sum(), purchases);
        dao.saveSale(sale);
        items.clear();
    }

    private boolean itemIsInCart(SoldItem item) {
        return items.stream().anyMatch(e -> e.getName().equals(item.getName()));
    }

    private boolean moreOfTheItemCanBeAdded(SoldItem item) {
        int cartQuantity = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().get().getQuantity();
        int stockQuantity = dao.findStockItem(item.getId()).getQuantity();
        return item.getQuantity() <= stockQuantity - cartQuantity;
    }

    private boolean quantityOfItemCanBeAdded(SoldItem item) {
        return item.getQuantity() <= item.getStockItem().getQuantity();
    }

    private boolean quantityIsNegativeOrZero(int quantity) {
        return quantity <= 0;
    }
}

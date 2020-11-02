package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
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
        if (!quantityOfItemCanBeAdded(item)) {
            throw new SalesSystemException("Desired quantity of " + item.getQuantity() + " exceeds the maximum quantity " +
            "of " + dao.findStockItem(item.getId()).getQuantity());
        }
        if (dao.findStockItem(item.getId()).getQuantity() < item.getQuantity()) {
            throw new SalesSystemException("Can't add product " + item.getName() + " with amount of " + item.getQuantity() +
                    " since there are only " + dao.findStockItem(item.getId()).getQuantity() + " units of this product.");
        }
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

    public void removeItemGUI(long id, int quantity) {
        if (items.stream().noneMatch(i -> i.getId() == id)) {
            throw new SalesSystemException("There aren't any items with ID of " + id + " in the cart");
        }
        SoldItem item = items.stream().filter(i -> i.getId() == id).findFirst().get();
        if (item.getQuantity() < quantity) {
            items.remove(dao.findSoldItem(id));
            throw new SalesSystemException("There aren't that many units of " + dao.findSoldItem(id) + " in the cart. " +
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

    public void submitCurrentPurchaseGUI() {
        Transaction transaction = dao.beginTransaction();
        List<Purchase> purchases = new ArrayList<>();
        for (SoldItem item : items) {
            purchases.add(new Purchase(item.getId(), item.getName(), item.getPrice(), item.getQuantity()));
            dao.findStockItem(item.getId()).lowerQuantity(item.getQuantity());
            if (dao.findStockItem(item.getId()).getQuantity() == 0) {
                StockItem stockItem = dao.findStockItem(item.getId());
                dao.findStockItems().remove(stockItem);
            }
        }
        transaction.setTotalQuantity(items.stream().mapToLong(SoldItem::getQuantity).sum());
        transaction.setPurchases(purchases);
        dao.commitTransaction();
        items.clear();
    }

    public void submitCurrentPurchaseCLI() {
        // TODO decrease quantities of the warehouse stock
        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        try {
            Transaction transaction = dao.beginTransaction();
            List<Purchase> purchases = new ArrayList<>();
            System.out.println("Are you sure that you want to submit current purchase? (Yes/No)");
            Scanner choice = new Scanner(System.in);
            String input = choice.nextLine().toLowerCase();
            if (input.equals("yes")) {
                if (!(items.isEmpty())) {
                    List<StockItem> stockItems = dao.findStockItems();
                    for (SoldItem item : items) {
                        dao.saveSoldItem(item);
                        String name = item.getName();
                        long idx = 0;
                        for (StockItem value : stockItems) {
                            if (name.equals(value.getName()))
                                idx = value.getId();
                        }
                        StockItem stockItem = dao.findStockItem(idx);
                        int soldAmount = item.getQuantity();
                        int amount = stockItem.getQuantity();
                        int newAmount = amount - soldAmount;
                        if (newAmount < 0) {
                            log.info("Removable amount can't exceed item quantity.");
                            dao.rollbackTransaction();
                            throw new SalesSystemException("Removable amount excceeds item quantity.");
                        } else if (newAmount == 0) {
                            stockItems.remove(stockItem);
                            log.info("All of the product (id: " + idx + ") has been removed from the warehouse.");
                            Purchase purchase = new Purchase(idx, name, stockItem.getPrice(), soldAmount);
                            purchases.add(purchase);
                        } else {
                            stockItem.setQuantity(newAmount);
                            log.info(soldAmount + " units of the product (id: " + idx + ") was removed from the warehouse.");
                            Purchase purchase = new Purchase(idx, name, stockItem.getPrice(), soldAmount);
                            purchases.add(purchase);

                        }
                    }
                    transaction.setPurchases(purchases);
                    transaction.setTotalQuantity(items.stream().mapToLong(SoldItem::getQuantity).sum());
                    dao.commitTransaction();
                    log.info("Purchase is completed. ");
                    items.clear();
                } else {
                    System.out.println("Cart is empty. ");
                }
            }
        } catch (Exception e) {
            dao.rollbackTransaction();
        }
    }

    private boolean itemIsInCart(SoldItem item) {
        return items.stream().anyMatch(e -> e.getName().equals(item.getName()));
    }

    private boolean quantityOfItemCanBeAdded(SoldItem item) {
        return item.getQuantity() <= item.getStockItem().getQuantity();
    }
}

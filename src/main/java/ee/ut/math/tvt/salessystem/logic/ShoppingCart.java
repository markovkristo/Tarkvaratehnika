package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);
    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private Warehouse warehouse = new Warehouse();

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
            "of " + dao.findStockItem(item.getStockItem().getIndex()).getQuantity());
        }
        if (itemIsInCart(item)) {
            if (moreOfTheItemCanBeAdded(item)) {
                SoldItem existingItem = items
                        .stream()
                        .filter(i -> i.getName().equals(item.getName()))
                        .findFirst()
                        .get();
                existingItem.addMoreQuantity(item.getQuantity());
                StockItem stockItem = dao.findStockItem(item.getStockItem().getIndex());
                stockItem.lowerQuantity(item.getQuantity());
                dao.removeAmountOfStockItem(stockItem);
            } else {
                int remainingQuantity = dao.findStockItem(item.getStockItem().getIndex()).getQuantity();
                throw new SalesSystemException("Can't add " + item.getQuantity() + " of " + item.getName() + " to the cart.\nThis " +
                        "exceeds the remaining quantity of " + remainingQuantity + ".");
            }
        } else {
            items.add(item);
            StockItem stockItem = dao.findStockItem(item.getStockItem().getIndex());
            if (item.getQuantity() < dao.findStockItem(item.getStockItem().getIndex()).getQuantity()) {
                stockItem.lowerQuantity(item.getQuantity());
                dao.removeAmountOfStockItem(stockItem);
            } else {
                stockItem.setQuantity(0);
                dao.removeStockItemEntirely(stockItem);
            }
        }
        log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public void removeItemGUI(long id, int quantity) {
        if (quantityIsNegativeOrZero(quantity)) {
            throw new SalesSystemException("Removable quantity can't be zero or negative.");
        }
        if (items.stream().noneMatch(i -> i.getStockItem().getIndex() == id)) {
            throw new SalesSystemException("There aren't any items with ID of " + id + " in the cart.");
        }
        SoldItem item = items.stream().filter(i -> i.getStockItem().getIndex() == id).findFirst().get();
        if (item.getQuantity() < quantity) {
            String name = item.getName();
            items.removeIf(i -> i.getStockItem().getIndex() == id);
            throw new SalesSystemException("There aren't that many units of " + name + " in the cart. " +
                    "All of the product was removed from the cart.");
        }
        items.stream().filter(i -> i.getStockItem().getIndex() == id).findFirst().get().lowerQuantity(quantity);
        if (items.stream().filter(i -> i.getStockItem().getIndex() == id).findFirst().get().getQuantity() == 0) {
            items.removeIf(i -> i.getStockItem().getIndex() == id);
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

    public void cancelCurrentPurchaseCLI() {
        for (SoldItem item : items) {
            StockItem stockItem = item.getStockItem();
            stockItem.addQuantity(item.getQuantity());
        }
        items.clear();
    }

    public void cancelCurrentPurchaseGUI(){
        for (SoldItem item: items) {
            StockItem stockItem = item.getStockItem();
            stockItem.setQuantity(item.getQuantity());
            warehouse.addItemToWarehouse(stockItem, dao);
        }
        items.clear();
    }

    public void submitCurrentPurchase() {
        for (SoldItem item : items) {
            dao.saveSoldItem(item);
        }
        Sale sale = new Sale(items.stream().mapToLong(SoldItem::getQuantity).sum(), items);
        dao.saveSale(sale);
        items.clear();
    }

    private boolean itemIsInCart(SoldItem item) {
        return items.stream().anyMatch(e -> e.getName().equals(item.getName()));
    }

    private boolean moreOfTheItemCanBeAdded(SoldItem item) {
        int stockQuantity = dao.findStockItem(item.getStockItem().getIndex()).getQuantity();
        return item.getQuantity() <= stockQuantity;
    }

    private boolean quantityOfItemCanBeAdded(SoldItem item) {
        return item.getQuantity() <= item.getStockItem().getQuantity();
    }

    private boolean quantityIsNegativeOrZero(int quantity) {
        return quantity <= 0;
    }
}

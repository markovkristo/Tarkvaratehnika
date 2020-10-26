package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;

public class Warehouse {

    private static final Logger log = LogManager.getLogger(Warehouse.class);

    public void addItemToWarehouse(StockItem item, StockItem addedItem, List<StockItem> stockItems) {
        if (itemExistsInWarehouseGUI(item) && itemHasSameName(item, addedItem.getName())) {
            addExistingItemToWarehouseGUI(item, addedItem.getQuantity(), addedItem.getPrice());
        } else if (itemExistsInWarehouseGUI(item) && !itemHasSameName(item, addedItem.getName())) {
            throw new SalesSystemException("Item with id: " + item.getId() +" is already in use ("+item.getName()+"). Please select a different" +
                    " id");
        } else {
            addNewItemToWarehouseGUI(addedItem, stockItems);
        }
    }

    public void removeItemFromWarehouse(List<StockItem> stockItems, long id, int removableQuantity) {
        if (!productWithGivenIdExists(id, stockItems)) {
            throw new SalesSystemException("There is no product with id of " + id);
        }
        StockItem item = stockItems.stream().filter(e -> e.getId() == id).findFirst().get();
        if (removableQuantityIsValid(removableQuantity, item.getQuantity())) {
            removeItemQuantity(stockItems, item, removableQuantity);
        } else {
            throw new SalesSystemException("Removable quantity ("+removableQuantity+") can not exceed maximum quantity" +
                    " ("+item.getQuantity()+")\nof product " + item.getName());
        }
    }

    private boolean itemExistsInWarehouseGUI(StockItem item) {
        return item != null;
    }

    private boolean itemHasSameName(StockItem item, String name) {
        return item.getName().equals(name);
    }

    private void addNewItemToWarehouseGUI(StockItem addedItem, List<StockItem> stockItems) {
        stockItems.add(addedItem);
        stockItems.sort(Comparator.comparing(StockItem::getId));
    }

    private void addExistingItemToWarehouseGUI(StockItem item, int quantity, double price) {
        if (item.getPrice() != price) {
            item.setPrice(price);
        }
        item.setQuantity(item.getQuantity() + quantity);
    }

    private boolean removableQuantityIsValid(int removableQuantity, int realQuantity) {
        return removableQuantity <= realQuantity;
    }

    private boolean productWithGivenIdExists(long id, List<StockItem> stockItems) {
        return stockItems.stream().anyMatch(e -> e.getId() == id);
    }

    private void removeItemQuantity(List<StockItem> stockItems, StockItem item, int quantity) {
        if (item.getQuantity() - quantity == 0) {
            stockItems.remove(item);
            String message = "All of the product (id: " + item.getId() + ") has been removed from the warehouse.";
            log.info(message);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
            String message = quantity + " units of the product (id: " + item.getId() + ") was removed from the warehouse";
            log.info(message);
        }
    }
}

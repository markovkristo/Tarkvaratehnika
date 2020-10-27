package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.Comparator;
import java.util.List;

public class Warehouse {

    public void addItemToWarehouse(StockItem item, StockItem addedItem, List<StockItem> stockItems) {
        if (!chosenDataIsValidForAdding(
                addedItem.getId(),
                addedItem.getName(),
                addedItem.getQuantity(),
                addedItem.getPrice())
        ) {
            return;
        }
        if (!chosenQuantityIsValid(addedItem.getQuantity())) {
            throw new SalesSystemException("Must choose a realistic quantity. Currently you chose: "
                    + addedItem.getQuantity());
        }
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
        if (!chosenDataIsValidForRemoval(id, removableQuantity)) {
            return;
        }
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
        addedItem.setName(addedItem.getName().trim());
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
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }
    }

    private boolean chosenQuantityIsValid(int quantity) {
        return quantity > 0;
    }

    private boolean chosenPriceIsValid(double price) {
        return price > 0;
    }

    private boolean chosenIdIsNotNegative(long id) {
        return id > 0;
    }

    private boolean chosenNameIsNotEmpty(String name) {
        return !name.replaceAll("\\s+","").equals("");
    }

    private boolean chosenDataIsValidForAdding(long id, String name, int quantity, double price) {
        if (!chosenIdIsNotNegative(id)) {
            throw new SalesSystemException("Must choose an ID that is positive. Currently you chose: "
                    + id);
        } else if (!chosenQuantityIsValid(quantity)) {
            throw new SalesSystemException("Must choose a realistic quantity. Currently you chose: "
                    + quantity);
        } else if (!chosenNameIsNotEmpty(name)) {
            throw new SalesSystemException("Must choose a name that contains characters.");
        } else if (!chosenPriceIsValid(price)) {
            throw new SalesSystemException("Must choose a price that is larger than 0 euros.");
        }
        return true;
    }

    private boolean chosenDataIsValidForRemoval(long id, int quantity) {
        if (!chosenIdIsNotNegative(id)) {
            throw new SalesSystemException("Must choose an ID that is positive. Currently you chose: "
                    + id);
        } else if (!chosenQuantityIsValid(quantity)) {
            throw new SalesSystemException("Must choose a realistic quantity. Currently you chose: "
                    + quantity);
        }
        return true;
    }
}

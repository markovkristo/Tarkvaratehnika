package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.util.List;

public class Warehouse {

    public void addItemToWarehouse(StockItem addedItem, SalesSystemDAO dao) {
        List<StockItem> stockItems = dao.findStockItems();
        StockItem item = stockItems.stream().filter(i -> i.getIndex().equals(addedItem.getIndex())).findAny().orElse(null);
        if (!chosenDataIsValidForAdding(addedItem.getIndex(), addedItem.getName(), addedItem.getQuantity(), addedItem.getPrice())) {
            return;
        }
        if (!chosenQuantityIsValid(addedItem.getQuantity())) {
            throw new SalesSystemException("Must choose a realistic quantity. Currently you chose: "
                    + addedItem.getQuantity());
        }
        if (item != null && item.getName().equals(addedItem.getName())) {
            StockItem existingStockItem = dao.findStockItem(addedItem.getIndex());
            existingStockItem.addQuantity(addedItem.getQuantity());
            existingStockItem.setPrice(addedItem.getPrice());
            dao.saveExistingStockItem(existingStockItem);
        } else if (item != null && !item.getName().equals(addedItem.getName())) {
            throw new SalesSystemException("Item with id: " + item.getIndex() +" is already in use ("+item.getName()+"). Please select a different" +
                    " id");
        } else {
            addedItem.setName(addedItem.getName().trim());
            dao.saveNewStockItem(addedItem);
        }
    }

    public void removeItemFromWarehouse(long id, int removableQuantity, SalesSystemDAO dao) {
        List<StockItem> stockItems = dao.findStockItems();
        if (!chosenDataIsValidForRemoval(id, removableQuantity)) {
            return;
        }
        if (!productWithGivenIdExists(id, stockItems)) {
            throw new SalesSystemException("There is no product with id of " + id);
        }
        StockItem item = stockItems.stream().filter(e -> e.getIndex() == id).findFirst().get();
        if (removableQuantity <= item.getQuantity()) {
            dao.removeStockItem(item, removableQuantity);
        } else {
            throw new SalesSystemException("Removable quantity ("+removableQuantity+") can not exceed maximum quantity" +
                    " ("+item.getQuantity()+")\nof product " + item.getName());
        }
    }

    private boolean productWithGivenIdExists(long id, List<StockItem> stockItems) {
        return stockItems.stream().anyMatch(e -> e.getIndex() == id);
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

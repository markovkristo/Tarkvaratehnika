package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Assert;;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WarehouseFunctionalityTests {

    private Warehouse warehouse = new Warehouse();


    @Test
    public void testAddingNewItem() {
        InMemorySalesSystemDAO dao = Mockito.spy(InMemorySalesSystemDAO.class);
        dao.addTemporaryItems();
        long itemId = 5L;
        System.out.println(dao.findStockItems());
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", 5.2, 10);
        List<StockItem> stockItems = dao.findStockItems();
        int currentSize = stockItems.size();
        warehouse.addItemToWarehouse(dao.findStockItem(itemId), addedItem, stockItems);
        List<StockItem> stockItemsAfterAddingNewItem = dao.findStockItems();
        Assert.assertEquals(1, stockItemsAfterAddingNewItem.size() - currentSize);
        Assert.assertEquals("Burger", dao.findStockItem(itemId).getName());
        Assert.assertEquals("Juicy", dao.findStockItem(itemId).getDescription());
        Assert.assertEquals(5.2, dao.findStockItem(itemId).getPrice(), 0);
        Assert.assertEquals(10, dao.findStockItem(itemId).getQuantity());
    }

    @Test
    public void testAddingExistingItem() {
        InMemorySalesSystemDAO dao = Mockito.spy(InMemorySalesSystemDAO.class);
        dao.addTemporaryItems();
        StockItem addedItem = new StockItem(1L, "Lays chips", "", 8, 15);
        StockItem item = dao.findStockItem(1L);
        List<StockItem> stockItems = dao.findStockItems();
        int currentSize = stockItems.size();
        int currentQuantity = item.getQuantity();
        double currentPrice = item.getPrice();
        warehouse.addItemToWarehouse(item, addedItem, stockItems);
        List<StockItem> stockItemsAfterAddingExistingItem = dao.findStockItems();
        Assert.assertEquals(currentSize, stockItemsAfterAddingExistingItem.size());
        Assert.assertTrue(item.getPrice() != currentPrice);
        Assert.assertTrue(item.getQuantity() > currentQuantity);
        verify(dao, times(0)).saveStockItem(addedItem);
    }
}

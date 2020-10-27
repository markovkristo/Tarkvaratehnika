package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WarehouseFunctionalityTests {

    private Warehouse warehouse = new Warehouse();

    private InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();

    @Test
    public void testAddingNewItem() {
        long itemId = 5L;
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
        InMemorySalesSystemDAO mockDao = Mockito.spy(InMemorySalesSystemDAO.class);
        mockDao.addTemporaryItems();
        StockItem addedItem = new StockItem(1L, "Lays chips", "", 8, 15);
        StockItem item = mockDao.findStockItem(1L);
        List<StockItem> stockItems = mockDao.findStockItems();
        int currentSize = stockItems.size();
        int currentQuantity = item.getQuantity();
        double currentPrice = item.getPrice();
        warehouse.addItemToWarehouse(item, addedItem, stockItems);
        List<StockItem> stockItemsAfterAddingExistingItem = mockDao.findStockItems();
        Assert.assertEquals(currentSize, stockItemsAfterAddingExistingItem.size());
        Assert.assertTrue(item.getPrice() != currentPrice);
        Assert.assertTrue(item.getQuantity() > currentQuantity);
        verify(mockDao, times(0)).saveStockItem(addedItem);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithNegativeQuantity() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", 5.2, -2);
        List<StockItem> stockItems = dao.findStockItems();
        warehouse.addItemToWarehouse(dao.findStockItem(itemId), addedItem, stockItems);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithNegativePrice() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
        List<StockItem> stockItems = dao.findStockItems();
        warehouse.addItemToWarehouse(dao.findStockItem(itemId), addedItem, stockItems);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithEmptyName() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "", "Juicy", 5.2, 10);
        List<StockItem> stockItems = dao.findStockItems();
        warehouse.addItemToWarehouse(dao.findStockItem(itemId), addedItem, stockItems);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithNegativeId() {
        long itemId = -5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
        List<StockItem> stockItems = dao.findStockItems();
        warehouse.addItemToWarehouse(dao.findStockItem(itemId), addedItem, stockItems);
    }
}

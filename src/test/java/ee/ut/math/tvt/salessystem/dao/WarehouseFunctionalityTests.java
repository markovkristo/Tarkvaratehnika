package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WarehouseFunctionalityTests {

    private Warehouse warehouse = new Warehouse();

    private InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();

    private StockItem addedItem = new StockItem(1L, "Lays chips", "", 8, 15);


    @Test (expected = AssertionError.class)
    public void testAddingItemBeginsAndCommitsTransaction(){
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        InOrder orderVerifier = Mockito.inOrder(mockDao);
        warehouse.addItemToWarehouse(addedItem, mockDao);
        orderVerifier.verify(mockDao).beginTransaction();
        orderVerifier.verify(mockDao).commitTransaction();
        verify(mockDao, times(1)).beginTransaction();
        verify(mockDao, times(1)).commitTransaction();
    }

    @Test
    public void testAddingNewItem() {
        long itemId = 5L;
        Assert.assertNull(dao.findStockItem(itemId));
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", 5.2, 10);
        int currentSize = dao.findStockItems().size();
        warehouse.addItemToWarehouse(addedItem, dao);
        List<StockItem> stockItemsAfterAddingNewItem = dao.findStockItems();
        Assert.assertEquals(1, stockItemsAfterAddingNewItem.size() - currentSize);
        Assert.assertEquals("Burger", dao.findStockItem(itemId).getName());
        Assert.assertEquals("Juicy", dao.findStockItem(itemId).getDescription());
        Assert.assertEquals(5.2, dao.findStockItem(itemId).getPrice(), 0);
        Assert.assertEquals(10, dao.findStockItem(itemId).getQuantity());
    }

    @Test
    public void testAddingExistingItem() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        StockItem item = mockDao.findStockItem(1L);
        int currentSize = mockDao.findStockItems().size();
        int currentQuantity = item.getQuantity();
        double currentPrice = item.getPrice();
        warehouse.addItemToWarehouse(addedItem, mockDao);
        List<StockItem> stockItemsAfterAddingExistingItem = mockDao.findStockItems();
        Assert.assertEquals(currentSize, stockItemsAfterAddingExistingItem.size());
        Assert.assertTrue(item.getPrice() != currentPrice);
        Assert.assertTrue(item.getQuantity() > currentQuantity);
        verify(mockDao, times(0)).saveNewStockItem(addedItem);
    }

    @Test
    public void testAddingItemWithNegativeQuantity() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", 5.2, -2);
        try {
            warehouse.addItemToWarehouse(addedItem, dao);
            Assert.fail();
        } catch (SalesSystemException e) {
            Assert.assertEquals("Must choose a realistic quantity. Currently you chose: " + addedItem.getQuantity(), e.getMessage());
        }
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithNegativePrice() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
        warehouse.addItemToWarehouse(addedItem, dao);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithEmptyName() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "", "Juicy", 5.2, 10);
        warehouse.addItemToWarehouse(addedItem, dao);
    }

    @Test (expected = SalesSystemException.class)
    public void testAddingItemWithNegativeId() {
        long itemId = -5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
        warehouse.addItemToWarehouse(addedItem, dao);
    }
}

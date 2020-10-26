package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.Assert;
import org.junit.Test;

public class WarehouseFunctionalityTests {

    private SalesSystemDAO dao = new InMemorySalesSystemDAO();
    private StockItem item;

    @Test
    public void testAddingNewItem() {
        long itemId = 5L;
        item = new StockItem(itemId, "Burger", "Juicy", 5.2, 10);
        dao.saveStockItem(item);

        Assert.assertEquals("Burger", dao.findStockItem(itemId).getName());
        Assert.assertEquals("Juicy", dao.findStockItem(itemId).getDescription());
        Assert.assertEquals(5.2, dao.findStockItem(itemId).getPrice(), 0);
        Assert.assertEquals(10, dao.findStockItem(itemId).getQuantity());
    }

    @Test
    public void testAddingExistingItem() {
        item = dao.findStockItems().stream().findAny().orElse(dao.findStockItems().get(0));

    }
}

package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class FunctionalityTests {

    private Warehouse warehouse = new Warehouse();
    private InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();
    private StockItem addedItem = new StockItem(1L, "Lays chips", "", 8, 15);
    private ShoppingCart shoppingCart = new ShoppingCart(dao);

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

    @Test
    public void testAddingItemWithNegativePrice() {
        long itemId = 5L;
        StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
        try {
            warehouse.addItemToWarehouse(addedItem, dao);
            Assert.fail();
        } catch (SalesSystemException e) {
            Assert.assertEquals("Must choose a price that is larger than 0 euros.", e.getMessage());
        }
    }

    @Test
    public void testAddingItemWithEmptyName() {
        long itemId = 5L;
        try {
            StockItem addedItem = new StockItem(itemId, "", "Juicy", 5.2, 10);
            warehouse.addItemToWarehouse(addedItem, dao);
        } catch (SalesSystemException e) {
            Assert.assertEquals("Must choose a name that contains characters.", e.getMessage());
        }
    }

    @Test
    public void testAddingItemWithNegativeId() {
        long itemId = -5L;
        try {
            StockItem addedItem = new StockItem(itemId, "Burger", "Juicy", -5.2, 10);
            warehouse.addItemToWarehouse(addedItem, dao);
        } catch (SalesSystemException e) {
            Assert.assertEquals("Must choose an ID that is positive. Currently you chose: " + itemId, e.getMessage());
        }
    }

    @Test
    public void testAddingItemWithQuantityTooLarge() {
        int quantity = 1000;
        StockItem stockItem = dao.findStockItem(4L);
        try {
            SoldItem item = new SoldItem(stockItem, quantity);
            shoppingCart.addItem(item);
        } catch (SalesSystemException e) {
            Assert.assertEquals("Desired quantity of "
                    + quantity + " exceeds the maximum quantity of " + stockItem.getQuantity(), e.getMessage());
        }
    }

    @Test
    public void testAddingItemWithQuantitySumTooLarge() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        int validQuantity = 1;
        int exceedingQuantity = 1000;
        StockItem stockItem = mockDao.findStockItem(4L);
        try {
            SoldItem item = new SoldItem(stockItem, validQuantity);
            testCart.addItem(item);
            SoldItem additionalItem = new SoldItem(stockItem, exceedingQuantity);
            testCart.addItem(additionalItem);
        } catch (SalesSystemException e) {
            Assert.assertEquals("Can't add " + exceedingQuantity + " of Free Beer to the cart.\n" +
                    "This exceeds the remaining quantity of " + stockItem.getQuantity() + ".", e.getMessage());
        }
    }

    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem = mockDao.findStockItem(4L);
        int quantityBeforePurchase = stockItem.getQuantity();

        SoldItem item = new SoldItem(stockItem, 3);
        testCart.addItem(item);
        testCart.submitCurrentPurchase();
        StockItem stockItemAfterPurchase = mockDao.findStockItem(4L);
        int quantityAfterPurchase = stockItemAfterPurchase.getQuantity();

        Assert.assertTrue(quantityBeforePurchase > quantityAfterPurchase);
    }

    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem1 = mockDao.findStockItem(4L);
        int firstItemQuantity = 1;
        StockItem stockItem2 = mockDao.findStockItem(3L);
        int secondItemQuantity = 2;

        SoldItem soldItem1 = new SoldItem(stockItem1, firstItemQuantity);
        SoldItem soldItem2 = new SoldItem(stockItem2, secondItemQuantity);
        testCart.addItem(soldItem1);
        testCart.addItem(soldItem2);
        testCart.submitCurrentPurchase();

        List<Sale> transactions = mockDao.findAllTransactions();
        Assert.assertEquals(1, transactions.size());
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().anyMatch(i -> i.getName().equals(stockItem1.getName())));
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().anyMatch(i -> i.getName().equals(stockItem2.getName())));
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().filter(i -> i.getName().equals(stockItem1.getName()))
            .anyMatch(i -> i.getQuantity().equals(firstItemQuantity)));
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().filter(i -> i.getName().equals(stockItem2.getName()))
                .anyMatch(i -> i.getQuantity().equals(secondItemQuantity)));
    }

    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem = mockDao.findStockItem(4L);
        SoldItem soldItem = new SoldItem(stockItem, 2);
        testCart.addItem(soldItem);
        testCart.submitCurrentPurchase();
        LocalTime currentTime = LocalTime.now();
        List<Sale> transactions = mockDao.findAllTransactions();

        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(currentTime, transactions.get(0).getTimeOfTransaction());
    }

    @Test
    public void testCancellingOrder() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem1 = mockDao.findStockItem(4L);
        int firstItemQuantity = 1;
        StockItem stockItem2 = mockDao.findStockItem(3L);
        int secondItemQuantity = 2;

        SoldItem soldItem1 = new SoldItem(stockItem1, firstItemQuantity);
        SoldItem soldItem2 = new SoldItem(stockItem2, secondItemQuantity);

        testCart.addItem(soldItem1);
        testCart.cancelCurrentPurchaseCLI();
        testCart.addItem(soldItem2);
        testCart.submitCurrentPurchase();

        List<Sale> transactions = mockDao.findAllTransactions();
        Assert.assertEquals(1, transactions.size());
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().noneMatch(i -> i.getName().equals(stockItem1.getName())));
        Assert.assertTrue(transactions.get(0).getSoldItems().stream().anyMatch(i -> i.getName().equals(stockItem2.getName())));
    }

    @Test
    public void testCancellingOrderQuanititesUnchanged() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem = mockDao.findStockItem(4L);
        int quantityBeforePurchase = stockItem.getQuantity();

        SoldItem soldItem = new SoldItem(stockItem, 2);
        testCart.addItem(soldItem);
        testCart.cancelCurrentPurchaseCLI();

        StockItem stockItemAfterCancel = mockDao.findStockItem(4L);
        int quantityAfterCancel = stockItemAfterCancel.getQuantity();

        Assert.assertEquals(quantityBeforePurchase, quantityAfterCancel);
    }

    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction() {
        InMemorySalesSystemDAO mockDao = Mockito.spy(new InMemorySalesSystemDAO());
        InOrder orderVerifier = Mockito.inOrder(mockDao);

        ShoppingCart testCart = new ShoppingCart(mockDao);
        StockItem stockItem = mockDao.findStockItem(4L);
        SoldItem soldItem = new SoldItem(stockItem, 2);
        testCart.addItem(soldItem);
        testCart.submitCurrentPurchase();

        orderVerifier.verify(mockDao).beginTransaction();
        orderVerifier.verify(mockDao).commitTransaction();
        verify(mockDao, times(1)).beginTransaction();
        verify(mockDao, times(1)).commitTransaction();
    }
}

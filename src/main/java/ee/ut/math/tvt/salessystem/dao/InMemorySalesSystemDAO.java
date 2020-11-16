package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Transaction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private List<StockItem> stockItemList;
    private List<SoldItem> soldItemList;
    private List<Transaction> transactionList;
    private Transaction transaction;

    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    public List<Transaction> findTransactions(){ return transactionList;}
    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getIndex() == id)
                return item;
        }
        return null;
    }

    public SoldItem findSoldItem(long id){
        for (SoldItem item : soldItemList) {
            if(item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {

    }

    @Override
    public void saveNewStockItem(StockItem item) {
        stockItemList.add(item);
        stockItemList.sort(Comparator.comparing(StockItem::getIndex));
    }

    @Override
    public void saveExistingStockItem(StockItem item) {

    }

    @Override
    public void removeStockItem(StockItem stockItem, int quantity) {

    }

    @Override
    public void beginTransaction() {
        //List<Purchase> purchases = new ArrayList<>();
        //transaction = new Transaction(java.time.LocalDate.now(), java.time.LocalTime.now(), 0L ,purchases);
        //return transaction;
    }

    @Override
    public void rollbackTransaction() {
        transactionList.remove(transaction);
        beginTransaction();
    }

    @Override
    public void commitTransaction() {
        transactionList.add(transaction);
    }
}

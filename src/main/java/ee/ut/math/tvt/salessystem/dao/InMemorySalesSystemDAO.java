package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private List<StockItem> stockItemList;
    private List<SoldItem> soldItemList;
    private List<Sale> sales;
    private Sale sale;

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

    @Override
    public List<Sale> findTransactionsBetween(LocalDate startDate, LocalDate endDate) {
        return null;
    }

    @Override
    public List<Sale> findLastTenTransactions() {
        return null;
    }

    @Override
    public List<Sale> findAllTransactions() {
        return null;
    }

    public List<Sale> findTransactions(){ return sales;}
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
    public void saveSale(Sale sale) {

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
    public void removeStockItemEntirely(StockItem stockItem) {

    }

    @Override
    public void removeAmountOfStockItem(StockItem stockItem) {

    }

    @Override
    public void beginTransaction() {
        //List<Purchase> purchases = new ArrayList<>();
        //transaction = new Transaction(java.time.LocalDate.now(), java.time.LocalTime.now(), 0L ,purchases);
        //return transaction;
    }

    @Override
    public void rollbackTransaction() {
        sales.remove(sale);
        beginTransaction();
    }

    @Override
    public void commitTransaction() {
        sales.add(sale);
    }
}

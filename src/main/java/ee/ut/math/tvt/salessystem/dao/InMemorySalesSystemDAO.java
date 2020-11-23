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
    private List<SoldItem> currentSoldItemList;
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
        this.sales = new ArrayList<>();
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public List<Sale> findTransactionsBetween(LocalDate startDate, LocalDate endDate) {
        List<Sale> matching = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getDateOfTransaction().compareTo(startDate) >= 0 && sale.getDateOfTransaction().compareTo(endDate) < 1) {
                matching.add(sale);
            }
        }
        return matching;
    }

    @Override
    public List<Sale> findLastTenTransactions() {
        List<Sale> lastTen = new ArrayList<>();
        for (int i = sales.size()-1; i > sales.size()-10; i--) {
            lastTen.add(sales.get(i));
        }
        return lastTen;
    }

    @Override
    public List<Sale> findAllTransactions() {
        return sales;
    }

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
    public void saveSoldItemsAsList(List<SoldItem> items) {
        this.currentSoldItemList = items;
        beginTransaction();
    }

    @Override
    public void saveSale(Sale sale) {
        this.sale = sale;
        commitTransaction();
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
    public void removeStockItemEntirely(StockItem item) {
        stockItemList.removeIf(i -> i.getIndex().equals(item.getIndex()));
    }

    @Override
    public void removeAmountOfStockItem(StockItem item) {

    }

    @Override
    public void beginTransaction() {
        soldItemList.addAll(currentSoldItemList);
    }

    @Override
    public void rollbackTransaction() {

    }

    @Override
    public void commitTransaction() {
        sales.add(sale);
    }
}

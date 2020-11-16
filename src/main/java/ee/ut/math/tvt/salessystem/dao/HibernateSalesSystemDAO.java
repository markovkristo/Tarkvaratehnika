package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {

    private final EntityManagerFactory emf;
    private final EntityManager em;

    public HibernateSalesSystemDAO() {
        emf = Persistence.createEntityManagerFactory("pos");
        em = emf.createEntityManager();
    }

    public void close() {
        em.close();
        emf.close();
    }

    @Override
    public List<StockItem> findStockItems() {
        beginTransaction();
        List<StockItem> stockItems = em.createQuery("FROM StockItem", StockItem.class).getResultList();
        commitTransaction();
        return stockItems;
    }

    @Override
    public List<Transaction> findTransactions() {
        return null;
    }

    @Override
    public StockItem findStockItem(long id) {
        StockItem stockItem = em.find(StockItem.class, id);
        em.detach(stockItem);
        return stockItem;
    }

    @Override
    public SoldItem findSoldItem(long id) {
        SoldItem soldItem = em.find(SoldItem.class, id);
        em.detach(soldItem);
        return soldItem;
    }

    @Override
    public void saveNewStockItem(StockItem stockItem) {
        beginTransaction();
        em.persist(stockItem);
        commitTransaction();
    }

    @Override
    public void saveExistingStockItem(StockItem stockItem) {
        beginTransaction();
        em.merge(stockItem);
        commitTransaction();
    }

    @Override
    public void removeStockItem(StockItem stockItem, int quantity) {

    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.persist(item);
    }

    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }

    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }
}

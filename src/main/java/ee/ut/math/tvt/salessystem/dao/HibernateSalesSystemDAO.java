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
        return em.createQuery("FROM StockItem", StockItem.class).getResultList();
    }

    @Override
    public List<Transaction> findTransactions() {
        return null;
    }

    @Override
    public StockItem findStockItem(long id) {
        return findStockItems()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public SoldItem findSoldItem(long id) {
        return em.createQuery("FROM StockItem", SoldItem.class)
                .getResultList()
                .stream()
                .filter(i -> i.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        em.persist(stockItem);
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

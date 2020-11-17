package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
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
    public List<Sale> findTransactionsBetween(LocalDate startDate, LocalDate endDate) {
        String queryString = "SELECT t FROM Sale as t WHERE t.dateOfTransaction >= :startDate AND c.dateOfTransaction <= :endDate";
        return em.createQuery(queryString, Sale.class).getResultList();
    }

    @Override
    public List<Sale> findLastTenTransactions() {
        String queryString = "SELECT t FROM Sale t ORDER BY t.timeOfTransaction DESC";
        return em.createQuery(queryString,Sale.class).setMaxResults(10).getResultList();
    }

    @Override
    public List<Sale> findAllTransactions() {
        return em.createQuery("FROM Sale", Sale.class).getResultList();
    }

    @Override
    public StockItem findStockItem(long id) {
        return findStockItems().stream().filter(i -> i.getIndex().equals(id)).findFirst().orElse(null);
    }

    @Override
    public SoldItem findSoldItem(long id) {
        beginTransaction();
        SoldItem soldItem = em.find(SoldItem.class, id);
        em.detach(soldItem);
        commitTransaction();
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
    public void removeStockItemEntirely(StockItem stockItem) {
        beginTransaction();
        em.remove(stockItem);
        commitTransaction();
    }

    @Override
    public void removeAmountOfStockItem(StockItem stockItem) {
        beginTransaction();
        em.merge(stockItem);
        commitTransaction();
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        beginTransaction();
        em.persist(item);
        commitTransaction();
    }

    @Override
    public void saveSale(Sale sale) {
        beginTransaction();
        em.persist(sale);
        commitTransaction();
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

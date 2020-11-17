package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;

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
    public List<Sale> findTransactions() {
        return null;
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

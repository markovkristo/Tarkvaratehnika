package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object for loading and saving sales system data.
 * Feel free to add methods here!
 * <p>
 * The sample implementation is called {@link InMemorySalesSystemDAO}. It allows you
 * to run the application without configuring a real database. Making changes is simple
 * and all data is lost when restarting the app. Later you will need to create a new
 * implementation {@link HibernateSalesSystemDAO} for SalesSystemDAO that uses a real
 * database to store the data. Keep the existing InMemorySalesSystemDAO implementation,
 * it will be useful when writing tests in lab6.
 * <p>
 * Implementations of this class must only handle storage/retrieval of the data.
 * Business logic and validation should happen in separate specialized classes.
 * Separating data access and business logic allows you to later test the business
 * logic using the InMemorySalesSystemDAO and avoid configuring the database for
 * each test (much faster and more convenient).
 * <p>
 * Note the transaction related methods. These will become relevant when you
 * start using a real database. Transactions allow you to group database operations
 * so that either all of them succeed or nothing at all is done.
 */
public interface SalesSystemDAO {

    List<StockItem> findStockItems();
    List<Sale> findTransactionsBetween(LocalDate startDate, LocalDate endDate);
    List<Sale> findLastTenTransactions();
    List<Sale> findAllTransactions();
    StockItem findStockItem(long id);

    SoldItem findSoldItem(long id);
    void saveNewStockItem(StockItem stockItem);

    void saveExistingStockItem(StockItem stockItem);

    void removeStockItemEntirely(StockItem stockItem);

    void removeAmountOfStockItem(StockItem stockItem);

    void saveSoldItem(SoldItem item);

    void saveSale(Sale sale);

    void beginTransaction();

    void rollbackTransaction();

    void commitTransaction();
}

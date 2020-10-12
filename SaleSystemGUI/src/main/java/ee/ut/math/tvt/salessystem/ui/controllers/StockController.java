package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final SalesSystemDAO dao;

    @FXML
    private Button addItem;
    @FXML
    private TableView<StockItem> warehouseTableView;
    @FXML
    private TextField barcode;
    @FXML
    private TextField amount;
    @FXML
    private TextField name;
    @FXML
    private TextField price;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        // TODO refresh view after adding new items
    }

    @FXML
    public void refreshButtonClicked() {
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }

    @FXML
    private void removeProductButtonClicked() {
        List<StockItem> stockItems = dao.findStockItems();
        long id = Long.parseLong(barcode.getText());
        int quantity = Integer.parseInt(amount.getText());
        StockItem item = dao.findStockItem(id);
        if (quantity > item.getQuantity()) {
            log.info("Removable amount can't exceed item quantity.");
        } else {
            if (item.getQuantity()-quantity == 0) {
                stockItems.remove(item);
                log.info("All of the product has been removed from the warehouse.");
            } else {
                item.setQuantity(item.getQuantity() - quantity);
                log.info(quantity + " units of the product was removed from the warehouse");
            }
            refreshStockItems();
        }
    }

    @FXML
    protected void addProductButtonClicked() {
        validateInstance();
        List<StockItem> stockItems = dao.findStockItems();
        long id = Long.parseLong(barcode.getText());
        String productName = name.getText();
        double productPrice = Double.parseDouble(price.getText());
        int quantity = Integer.parseInt(amount.getText());

        if (dao.findStockItem(id) == null) {
            StockItem item = new StockItem(id, productName, "", productPrice, quantity);
            stockItems.add(item);
            log.info("New product has been added / resupplied");
            refreshStockItems();
        } else {
            StockItem item = dao.findStockItem(id);
            if (!item.getName().equals(productName)) {
                log.info("There's a different item with the same index inside the warehouse");
            } else {
                if (item.getPrice() != productPrice) {
                    item.setPrice(productPrice);
                }
                item.setQuantity(item.getQuantity() + quantity);
                log.info("New product has been added / resupplied");
                refreshStockItems();
            }
        }
    }

    private void validateInstance() {
        Objects.requireNonNull(barcode.getText());
        Objects.requireNonNull(amount.getText());
        Objects.requireNonNull(name.getText());
        Objects.requireNonNull(price.getText());
    }
}

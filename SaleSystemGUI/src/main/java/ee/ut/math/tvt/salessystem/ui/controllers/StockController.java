package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);
    private final SalesSystemDAO dao;
    private final Warehouse warehouse = new Warehouse();

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
        if (!isInputDataValidForRemoval()) {
            log.error("Found invalid input data.");
            return;
        }
        List <StockItem> stockItems = dao.findStockItems();
        try {
            warehouse.removeItemFromWarehouse(
                    stockItems,
                    Long.parseLong(barcode.getText()),
                    Integer.parseInt(amount.getText())
            );
            log.debug("Item with id " + barcode.getText() + " and amount " + amount.getText() + " was removed");
            refreshStockItems();
        } catch (SalesSystemException e) {
            log.error("An exception thrown because of data entered, see info in pop-up.");
            display(e.getMessage());
        }
        log.info("Product removed from warehouse");
    }

    @FXML
    protected void addProductButtonClicked() {
        if (!isInputDataValidForAdding()) {
            log.error("Found invalid input data.");
            return;
        }
        StockItem addedItem = getAddedItemFromTextfields();
        List<StockItem> stockItems = dao.findStockItems();
        try {
            warehouse.addItemToWarehouse(dao.findStockItem(addedItem.getId()), addedItem, stockItems);
            log.debug("Item " + addedItem.toString() + " was added");
            refreshStockItems();
        } catch (SalesSystemException | NumberFormatException e) {
            log.error("An exception thrown because of data entered, see info in pop-up.");
            display(e.getMessage());
        }
        log.info("Product added to warehouse.");
    }

    private void display(String message) {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Notification");
        Label label1 = new Label(message);
        label1.autosize();
        Button button1 = new Button("Proceed");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 400, 250);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }

    private boolean isInputDataValidForRemoval() {
        try {
            Long.parseLong(barcode.getText());
            Integer.parseInt(amount.getText());
        } catch(NumberFormatException e) {
            display("You have entered invalid or missing data for one of the cells. \n" +
                    "Amount and barcode must be set.");
            return false;
        }
        return true;
    }

    private boolean isInputDataValidForAdding() {
        try {
            Long.parseLong(barcode.getText());
            Double.parseDouble(price.getText());
            Integer.parseInt(amount.getText());
        } catch(NumberFormatException e) {
            display("You have entered invalid or missing data for one of the cells. \n" +
                    "Amount, price, barcode and name must be set.");
            return false;
        }
        return true;
    }

    private StockItem getAddedItemFromTextfields() {
        return new StockItem(
                Long.parseLong(barcode.getText()),
                name.getText(),
                "",
                Double.parseDouble(price.getText()),
                Integer.parseInt(amount.getText()));
    }
}

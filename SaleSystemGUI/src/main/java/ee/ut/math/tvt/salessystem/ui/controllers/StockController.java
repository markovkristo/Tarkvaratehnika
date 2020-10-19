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
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

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
    private void removeProductButtonClicked() throws Exception{
        List<StockItem> stockItems = dao.findStockItems();
        long id = Long.parseLong(barcode.getText());
        int quantity = Integer.parseInt(amount.getText());
        StockItem item = dao.findStockItem(id);
        if (quantity > item.getQuantity()) {
            String message = "Removable amount can't exceed item quantity.";
            log.info(message);
            display(message);
        } else {
            if (item.getQuantity() - quantity == 0) {
                stockItems.remove(item);
                String message = "All of the product (id: " + id + ") has been removed from the warehouse.";
                log.info(message);
                display(message);
            } else {
                item.setQuantity(item.getQuantity() - quantity);
                String message = quantity + " units of the product (id: " + id + ") was removed from the warehouse";
                log.info(message);
                display(message);
            }
            refreshStockItems();
        }
    }

    @FXML
    protected void addProductButtonClicked() throws Exception{
        validateInstance();
        List<StockItem> stockItems = dao.findStockItems();
        long id = Long.parseLong(barcode.getText());
        String productName = name.getText();
        double productPrice = Double.parseDouble(price.getText());
        int quantity = Integer.parseInt(amount.getText());

        if (dao.findStockItem(id) == null) {
            StockItem item = new StockItem(id, productName, "", productPrice, quantity);
            stockItems.add(item);
            String message = productName + ", " + quantity + " units - has been added to warehouse!";
            log.info(message);
            display(message);
            refreshStockItems();
            //Popup.display("Warehouse", "New product has been added / resupplied!", "Proceed");

        } else {
            StockItem item = dao.findStockItem(id);
            if (!item.getName().equals(productName)) {
                String message = "There's a different item with the same index inside the warehouse";
                log.info(message);
                display(message);
                //Popup.display("Error", "There's a different item with the same index inside the warehouse", "Proceed");
            } else {
                String message = productName + " has been resupplied by " + quantity + " units.";
                if (item.getPrice() != productPrice) {
                    item.setPrice(productPrice);
                    message = productName + " price has been updated to " + productPrice + " and added " + quantity + " units.";
                }
                item.setQuantity(item.getQuantity() + quantity);
                log.info(message);
                display(message);
                refreshStockItems();
                //Popup.display("Warehouse", "New product has been added / resupplied, price has been updated", "Proceed");
            }
        }
    }

    private void display(String message) throws Exception{
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Notification");
        Label label1 = new Label(message);
        Button button1 = new Button("Proceed");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 400, 250);
        //scene1.getStylesheets().add(getClass().getResource("main/ee/ut/math/tvt/salessystem/ui/DefaultTheme.css").toExternalForm());
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();

    }

    private void validateInstance() {
        Objects.requireNonNull(barcode.getText());
        Objects.requireNonNull(amount.getText());
        Objects.requireNonNull(name.getText());
        Objects.requireNonNull(price.getText());
    }
}

package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TableView<Sale> historyTableView;
    @FXML
    private TableView<SoldItem> historyTransactionView;
    private SalesSystemDAO dao;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void showAll() {
        List<Sale> sales = dao.findAllTransactions();
        if (sales.isEmpty()) {
            displayInfo("There are have not been any transactions.");
            return;
        }
        historyTableView.setItems(FXCollections.observableList(sales));
        log.info("All history is shown.");
    }

    @FXML
    public void showBetweenDates() {
        if (!dataIsPresent()) {
            displayInfo("One or both dates are empty. Please fill them.");
            log.debug("One or both dates are empty. Please fill them.");
            return;
        }
        LocalDate startDateInput = startDate.getValue();
        LocalDate endDateInput = endDate.getValue();
        List<Sale> sales = dao.findTransactionsBetween(startDateInput, endDateInput);
        List<Sale> transactionsBetweenDates = new ArrayList<>();
        for (Sale ts : sales) {
            LocalDate date = ts.getDateOfTransaction();
            if (date.compareTo(startDateInput) >= 0 && date.compareTo(endDateInput) <= 0)
                transactionsBetweenDates.add(ts);
        }
        if (transactionsBetweenDates.isEmpty()) {
            displayInfo("There have been no transactions between dates " + startDateInput + " and " + endDateInput);
            return;
        }
        historyTableView.setItems(FXCollections.observableList(transactionsBetweenDates));
        log.info("History between " + startDate.getValue() + " and " + endDate.getValue() + " is shown.");
    }

    @FXML
    public void showLastTen() {
        List<Sale> sales = dao.findLastTenTransactions();
        if (sales.isEmpty()) {
            displayInfo("There have not been any transactions.");
            return;
        }
        List<Sale> tenSales = new ArrayList<>();
        for (int i = sales.size()-1; i >= 0 ; i--) {
            tenSales.add(sales.get(i));
        }
        historyTableView.setItems(FXCollections.observableList(tenSales));
        log.info("History of last 10 transactions is shown.");
    }

    @FXML
    public void purchases(MouseEvent click) {
        Sale sale = historyTableView.getSelectionModel().getSelectedItem();
        historyTransactionView.setItems(FXCollections.observableList(sale.getSoldItems()));
    }

    private boolean dataIsPresent() {
        return startDate.getValue() != null && endDate.getValue() != null;
    }

    private void displayInfo(String message) {
        Stage popupwindow = new Stage();
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Purchasing notification");
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
}

package ee.ut.math.tvt.salessystem.ui.controllers;

import javafx.fxml.Initializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;



/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {
    private static final Logger log = LogManager.getLogger(HistoryController.class);
    private TextField startDate;
    private TextField endDate;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: implement
    }

    public void showAllHistoryButtonClicked() {
        log.info("All history is shown.");
    }

    public void showHistoryBetweenSpecificDatesButtonClicked() {
        log.info("History between " + startDate.getText() + " and " + endDate.getText() + " is shown.");
    }

    public void showLastTenPurchasesButtonClicked() {
        log.info("History of last 10 transactions is shown.");
    }


}

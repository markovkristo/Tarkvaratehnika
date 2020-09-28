package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dto.Transaction;

import java.util.List;

public class History {
    private String startDate;
    private String endDate;
    private List<Transaction> transaction;

    public History(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void showLastTenPurchases() {

    }

    public void showAllPurchases() {

    }

    public void showPurchaseHistoryBetweenDates() {

    }
}

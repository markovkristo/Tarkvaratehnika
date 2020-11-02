package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.Transaction;

import java.time.LocalDate;
import java.util.List;

public class History {
    private SalesSystemDAO dao;

    public History(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void showLastTenPurchases() {
        System.out.println("Last 10 purchases are: ");
        List<Transaction> transactions = dao.findTransactions();
        int size = transactions.size();
        if (transactions.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                Transaction ts = transactions.get(size - 1 - i);
                System.out.println(ts.toString() + " ");
            }
        } else if(transactions.size() > 0){
            for (int i = 0; i < transactions.size(); i++) {
                Transaction ts = transactions.get(size - 1 - i);
                System.out.println(ts.toString() + " ");
            }
        }else {
            System.out.println("History is empty. ");
        }
    }

    public void showAllPurchases() {
        System.out.println("All purchases are: ");
        List<Transaction> transactions = dao.findTransactions();
        for (Transaction ts : transactions) {
            System.out.println(ts.toString() + " ");
        }
    }

    public void showPurchaseHistoryBetweenDates(String startDate, String endDate) {
        System.out.println("All purchases which were made between " + startDate + " and " + endDate + ", were: ");
        List<Transaction> transactions = dao.findTransactions();
        for (Transaction ts : transactions) {
            LocalDate date = ts.getLocalDate();
            if (date.compareTo(LocalDate.parse(startDate)) > 0 && date.compareTo(LocalDate.parse(endDate)) < 0)
                System.out.print(ts.toString() + " ");
        }
    }

}

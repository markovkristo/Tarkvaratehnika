package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.Transaction;

import java.time.LocalDate;
import java.util.List;

public class History {
    private List<Transaction> transactions;
    private  SalesSystemDAO dao;

    public History(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void showLastTenPurchases(String startDate, String endDate) {
        System.out.println("Last 10 purchases are: ");
        int size = transactions.size();
        for (int i = 0; i < 10; i++) {
            Transaction ts = transactions.get(size - i);
            System.out.println(ts.toString() + " ");
        }
    }

    public void showAllPurchases() {
        System.out.println("All purchases are: ");
        for (Transaction ts: transactions){
            System.out.println(ts.toString() + " ");
        }
    }

    public void showPurchaseHistoryBetweenDates(String startDate, String endDate) {
        System.out.println("All purchases which were made between " + startDate + " and " + endDate + ", were: ");
        for (Transaction ts: transactions) {
            LocalDate date = ts.getLocalDate();
            if(date.compareTo(LocalDate.parse(startDate)) > 0 && date.compareTo(LocalDate.parse(endDate)) < 0)
                System.out.print(ts.toString() + " ");
        }
    }
}

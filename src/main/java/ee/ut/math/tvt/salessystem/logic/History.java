package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Sale;

import java.time.LocalDate;
import java.util.List;

public class History {
    private SalesSystemDAO dao;

    public History(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void showLastTenPurchasesCLI() {
        System.out.println("Last 10 purchases are: ");
        List<Sale> sales = dao.findLastTenTransactions();
        int size = sales.size();
        if (sales.size() >= 10) {
            for (int i = 0; i < 10; i++) {
                Sale ts = sales.get(size - 1 - i);
                System.out.println(ts.toString() + " ");
            }
        } else if(sales.size() > 0){
            for (int i = 0; i < sales.size(); i++) {
                Sale ts = sales.get(size - 1 - i);
                System.out.println(ts.toString() + " ");
            }
        }else {
            System.out.println("History is empty. ");
        }
    }

    public void showAllPurchasesCLI() {
        System.out.println("All purchases are: ");
        List<Sale> sales = dao.findAllTransactions();
        for (Sale ts : sales) {
            System.out.println(ts.toString() + " ");
        }
    }

    public void showPurchaseHistoryBetweenDatesCLI(LocalDate startDate, LocalDate endDate) {
        System.out.println("All purchases which were made between " + startDate + " and " + endDate + ", were: ");
        List<Sale> sales = dao.findTransactionsBetween(startDate, endDate);
        for (Sale ts : sales) {
            LocalDate date = ts.getDateOfTransaction();
            if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0)
                System.out.print(ts.toString() + " ");
        }
    }

}

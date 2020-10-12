package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.TeamInfoSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;
    private final TeamInfoSupplier teamInfoSupplier;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        teamInfoSupplier = new TeamInfoSupplier();
    }

    public static void main(String[] args) throws Exception {
        SalesSystemDAO dao = new InMemorySalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items, all items sum: " + (si.getQuantity() * si.getPrice()) + ")" + "");
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showCart() {
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (cart.getAll().size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\t\t\t\t\tShow this help");
        System.out.println("w\t\t\t\t\t\tShow warehouse contents");
        System.out.println("wa IDX NR Na Desc P\t\tAdd NR of items with index IDX, name Na, description Desc and price P to the warehouse");
        System.out.println("wr IDX NR \t\t\t\tRemove NR of stock item with index IDX from the warehouse");
        System.out.println("c\t\t\t\t\t\tShow cart contents");
        System.out.println("a IDX NR \t\t\t\tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\t\t\t\t\tPurchase the shopping cart");
        System.out.println("r\t\t\t\t\t\tReset the shopping cart");
        System.out.println("t\t\t\t\t\t\tSee team information");
        System.out.println("-------------------------");
    }

    private void showTeamInfo() {
        System.out.println("-------------------------");
        System.out.println("Team information:");
        System.out.println("Team name: " + teamInfoSupplier.getTeamName());
        System.out.println("Team leader: " + teamInfoSupplier.getTeamLeaderName());
        System.out.println("Team members: " + teamInfoSupplier.getTeamMembers());
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        String[] c = command.split(" ");

        if (c[0].equals("h"))
            printUsage();
        else if (c[0].equals("q"))
            System.exit(0);
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("t"))
            showTeamInfo();
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                } else {
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if (c[0].equals("wa") && c.length == 6) {
            try {
                long idx = Long.parseLong(c[1]);
                int quantity = Integer.parseInt(c[2]);
                String name = c[3];
                String desc = c[4];
                double price = Double.parseDouble(c[5]);
                List<StockItem> stockItems = dao.findStockItems();
                StockItem item = new StockItem(idx, name, desc, price, quantity );
                if (item != null) {
                    stockItems.add(item);
                } else {
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if (c[0].equals("wr") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int removableAmount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                int amount = item.getQuantity();
                Scanner choice= new Scanner(System.in);
                System.out.println("Are you sure that you want to remove this item from the warehouse? (Yes/No)");
                String input = choice.nextLine().toLowerCase();
                if (input.equals("yes")) {
                    if (item != null) {
                        item.setQuantity(amount - removableAmount);
                    } else {
                        System.out.println("No stock item with id " + idx);
                    }
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else {
            System.out.println("unknown command");
        }
    }
}

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
        double totalSum = 0;
        int totalProducts = 0;
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items, total: " + si.getPrice() * si.getQuantity() + ")" );
            totalSum += si.getQuantity() * si.getPrice();
            totalProducts += si.getQuantity();
        }
        System.out.println("Total products: " + totalProducts + ".");
        System.out.println("Total sum: " + totalSum + ".");
        if (cart.getAll().size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void addCart(String[] c){
        System.out.println("-------------------------");
        if(c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                List<StockItem> stockItems = dao.findStockItems();
                if (item != null) {
                    int newAmount = item.getQuantity() - amount;
                    if (newAmount < 0)
                        System.out.println("Exeeded the maximum quantity of item called " + item.getName() + " by " + (amount - item.getQuantity()));
                    else if (newAmount == 0) {
                        cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                        stockItems.remove(item);
                        System.out.println("Added " + amount + " " + item.getName() + " to the shopping cart.");
                    } else {
                        cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                        item.setQuantity(newAmount);
                        System.out.println("Added " + amount + " " + item.getName() + " to the shopping cart.");
                    }
                } else {
                    System.out.println("No stock item with id " + idx + ".");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if(c.length < 3){
            System.out.println("You didn't enter enough parameters. You have to enter item index and item quantity.");
        }
        else if(c. length > 3){
            System.out.println("You entered to many parameters. You have to enter item index and item quantity.");
        }
        System.out.println("-------------------------");
    }

    private void changePrice(String[] c) {
        System.out.println("-------------------------");
        if(c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                double price = Double.parseDouble(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    double oldPrice = item.getPrice();
                    item.setPrice(price);
                    System.out.println("Changed the price of " + item.getName() + " from " + oldPrice + " to " + price);
                } else {
                    System.out.println("No stock item with id " + idx + ".");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if(c.length < 3){
            System.out.println("You didn't enter enough parameters. You have to enter item index and items new price.");
        }
        else if(c. length > 3){
            System.out.println("You entered to many parameters. You have to enter item index and items new price.");
        }
        System.out.println("-------------------------");
    }
    private void addExistingItemToWarehouse(String[] c) {
        System.out.println("-------------------------");
        if(c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int quantity = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    item.setQuantity(item.getQuantity() + quantity);
                    System.out.println("Added " + quantity + " new " + item.getName() + " to warehouse. New total: " + item.getQuantity());
                } else {
                    System.out.println("No stock item with id " + idx + ".");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if(c.length < 3){
            System.out.println("You didn't enter enough parameters. You have to enter item index and item quantity.");
        }
        else if(c. length > 3){
            System.out.println("You entered to many parameters. You have to enter item index and item quantity.");
        }
        System.out.println("-------------------------");
    }

    private void addNewItemToWarehouse(String[] c) {
        System.out.println("-------------------------");
        if(c.length >= 6) {
            try {
                long idx = Long.parseLong(c[1]);
                int quantity = Integer.parseInt(c[2]);
                double price = Double.parseDouble(c[3]);
                String desc = c[4];
                String name = c[5];
                List<StockItem> stockItems = dao.findStockItems();
                StockItem item = dao.findStockItem(idx);
                if (item == null) {
                    StockItem newItem = new StockItem(idx, name, desc, price, quantity);
                    stockItems.add(newItem);
                    System.out.println("Added new item to " + newItem.getDescription() + " called " + newItem.getName() + " with id " + newItem.getId() + ", quantity: " + newItem.getQuantity() + " and price " + newItem.getPrice());
                }
                else {
                    System.out.println("Item with id " + idx + " already exists in warehouse. If you want to add an already existing item then use command wa");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if(c.length < 6){
            System.out.println("You didn't enter enough parameters. You have to enter new items index, item quantity, price, decription and name.");
        }
        System.out.println("-------------------------");
    }

    private void removeItemFromWarehouse(String[] c) {
        System.out.println("-------------------------");
        if(c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int removableAmount = Integer.parseInt(c[2]);
                StockItem item = dao.findStockItem(idx);
                List<StockItem> stockItems = dao.findStockItems();
                if (item != null) {
                    System.out.println("Are you sure that you want to remove this item from the warehouse? (Yes/No)");
                    Scanner choice = new Scanner(System.in);
                    String input = choice.nextLine().toLowerCase();
                    if (input.equals("yes")) {
                        int amount = item.getQuantity();
                        int newAmount = amount - removableAmount;
                        if (newAmount == 0) {
                            stockItems.remove(item);
                            System.out.println("Removed " + removableAmount + " " + item.getName() + " from warehouse. There is no more of this item in the warehouse. ");
                        }
                        else if (newAmount < 0)
                            System.out.println("There isn't so many items in the warehouse. Exeeded the maximum quanity by " + (removableAmount - amount) + ".");
                        else {
                            item.setQuantity(newAmount);
                            System.out.println("Removed " + removableAmount + " " + item.getName() + " from the warehouse.");
                        }
                    } else{
                        System.out.println("Didn't remove the item. ");
                    }
                }else {
                    System.out.println("No stock item with id " + idx + ".");
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }else if(c.length < 3){
            System.out.println("You didn't enter enough parameters. You have to enter item index and item quantity.");
        }
        else if(c. length > 3){
            System.out.println("You entered to many parameters. You have to enter item index and item quantity.");
        }
        System.out.println("-------------------------");
    }

    private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\t\t\t\t\tShow this help");
        System.out.println("w\t\t\t\t\t\tShow warehouse contents");
        System.out.println("wan,IDX,NR,P,Desc,Na\tAdd NR of new items with index IDX, price P, description Desc and name Na to the warehouse");
        System.out.println("wa,IDX,NR \t\t\t\tAdd NR of of already existing stock items with index IDX to the warehouse");
        System.out.println("wr,IDX,NR \t\t\t\tRemove NR of stock item with index IDX from the warehouse");
        System.out.println("cp,IDX,P \t\t\t\tChange price P  of stock item with index IDX");
        System.out.println("c\t\t\t\t\t\tShow cart contents");
        System.out.println("a,IDX,NR \t\t\t\tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\t\t\t\t\tPurchase the shopping cart");
        System.out.println("r\t\t\t\t\t\tReset the shopping cart");
        System.out.println("t\t\t\t\t\t\tSee team information");
        System.out.println("q\t\t\t\t\t\tQuit application");
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
        String[] a = command.split(",");
        String[] c = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i].trim();
        }
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
        else if (c[0].equals("cp")) {
            changePrice(c);
        }
        else if (c[0].equals("a")) {
            addCart(c);
        }
        else if (c[0].equals("wa")) {
            addExistingItemToWarehouse(c);
        }
        else if (c[0].equals("wan")) {
            addNewItemToWarehouse(c);

        }
        else if (c[0].equals("wr") ) {
            removeItemFromWarehouse(c);
        }
        else {
            System.out.println("unknown command");
        }
    }




}

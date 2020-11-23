package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.History;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.TeamInfoSupplier;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    private final History history;
    private final Warehouse warehouse;

    public ConsoleUI(SalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
        history = new History(dao);
        teamInfoSupplier = new TeamInfoSupplier();
        warehouse = new Warehouse();
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
        log.info("SalesSystem CLI started");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim());
            System.out.println("Done. ");
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkCommands2(String[] c) {
        if (c.length < 6) {
            System.out.println("You didn't enter enough parameters. You have to enter new item's index, item quantity, price, description and name.");
            return false;
        } else if (c.length > 6) {
            System.out.println("You entered too many parameters. You have to enter new item's index, item quantity, price, description and name.");
            return false;
        } else if (!(isNumeric(c[1]) && (isNumeric(c[2])) && isNumeric(c[3]))) {
            System.out.println("IDX, NR and P have to be numeric.");
            return false;
        } else if (Integer.parseInt(c[1]) < 0 || Integer.parseInt(c[2]) < 0 || Double.parseDouble(c[3]) < 0.0) {
            System.out.println("IDX, NR and P can't have negative values.");
            return false;
        } else
            return true;
    }

    private boolean checkCommands(String[] c) {
        if (c.length > 3) {
            System.out.println("You entered too many parameters. You have to enter item index and item quantity.");
            return false;
        } else if (c.length < 3) {
            System.out.println("You didn't enter enough parameters. You have to enter item index and item quantity.");
            return false;
        } else if (!(isNumeric(c[1]) && isNumeric(c[2]))) {
            System.out.println("IDX and NR have to be numeric. ");
            return false;
        } else if (Integer.parseInt(c[1]) < 0 || Integer.parseInt(c[2]) < 0) {
            System.out.println("IDX and P can't have negative values. ");
            return false;
        } else
            return true;
    }

    private boolean checkDates(String[] c) {
        if (c.length == 3) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dddd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(c[1].trim());
                dateFormat.parse(c[2].trim());
            } catch (ParseException pe) {
                System.out.println("The string is not a date. " + pe.getMessage());
                return false;
            }
        } else if (c.length < 3) {
            System.out.println("You didn't enter enough parameters. You have to enter start date and end date.");
            return false;
        } else if (c.length > 3) {
            System.out.println("You entered too many parameters. You have to enter start date and end date.");
            return false;
        }
        return true;
    }

    private boolean ifItemExists(StockItem item) {
        if (item != null)
            return true;
        else
            throw new SalesSystemException("There aren't any items with this ID.");
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        int empty = 0;
        for (StockItem si : stockItems) {
            if (si.getQuantity() != 0) {
                System.out.println(si.getIndex() + " " + si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items, all items sum: " + (si.getQuantity() * si.getPrice()) + ")" + "");
            } else {
                empty++;
            }
        }
        if (stockItems.size() == 0 || empty == stockItems.size()) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }


    private void showCart() {
        System.out.println("-------------------------");
        double totalSum = 0;
        int totalProducts = 0;
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + " Euro (" + si.getQuantity() + " items, total: " + si.getPrice() * si.getQuantity() + ")");
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

    private void addCart(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Long.parseLong(c[1]);
            int amount = Integer.parseInt(c[2]);
            StockItem item = dao.findStockItem(idx);
            cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
            log.info("Added " + amount + " " + item.getName() + " to the cart ");

        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void removeItemFromCart(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Integer.parseInt(c[1]);
            int quantity = Integer.parseInt(c[2]);
            cart.removeItem(idx, quantity);
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void changePrice(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Long.parseLong(c[1]);
            StockItem item = dao.findStockItem(idx);
            double newPrice = Double.parseDouble(c[2]);
            double oldPrice = item.getPrice();
            warehouse.changePrice(item, newPrice);
            log.info("Changed the price of " + item.getName() + " from " + oldPrice + " to " + newPrice + ".");
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void addExistingItemToWarehouse(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Long.parseLong(c[1]);
            int amount = Integer.parseInt(c[2]);
            StockItem item = dao.findStockItem(idx);
            StockItem newItem = new StockItem(idx, item.getName(), item.getDescription(), item.getPrice(), amount);
            warehouse.addItemToWarehouse(newItem, dao);
            log.info("Added " + amount + " new " + item.getName() + " to warehouse. New total: " + item.getQuantity());
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void addNewItemToWarehouse(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Long.parseLong(c[1]);
            int quantity = Integer.parseInt(c[2]);
            double price = Double.parseDouble(c[3]);
            String desc = c[4];
            String name = c[5];
            StockItem newItem = new StockItem(idx, name, desc, price, quantity);
            warehouse.addItemToWarehouse(newItem, dao);
            log.info("Added new item to " + newItem.getDescription() + " called " + newItem.getName() + " with id " + newItem.getIndex() + ", quantity: " + newItem.getQuantity() + " and price " + newItem.getPrice());
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void removeItemFromWarehouse(String[] c) {
        System.out.println("-------------------------");
        try {
            long idx = Long.parseLong(c[1]);
            int removableAmount = Integer.parseInt(c[2]);
            StockItem item = dao.findStockItem(idx);
            warehouse.removeItemFromWarehouse(idx, removableAmount, dao);
            log.info("Removed " + removableAmount + " " + item.getName() + " from warehouse. ");
        } catch (SalesSystemException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("-------------------------");
    }

    private void printWarehouseUsage(){
        System.out.println("-------------------------");
        System.out.println("w\t\t\t\t\t\tShow warehouse contents");
        System.out.println("wan,IDX,NR,P,Desc,Na\tAdd NR of new items with index IDX, price P, description Desc and name Na to the warehouse");
        System.out.println("wa,IDX,NR \t\t\t\tAdd NR of of already existing stock items with index IDX to the warehouse");
        System.out.println("wr,IDX,NR \t\t\t\tRemove NR of stock item with index IDX from the warehouse");
        System.out.println("cp,IDX,P \t\t\t\tChange price P  of stock item with index IDX");
        System.out.println("q\t\t\t\t\t\tQuit application");
        System.out.println("-------------------------");
    }

    private void printPOSUsage() {
        System.out.println("-------------------------");
        System.out.println("c\t\t\t\t\t\tShow cart contents");
        System.out.println("a,IDX,NR \t\t\t\tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\t\t\t\t\tPurchase the shopping cart");
        System.out.println("r\t\t\t\t\t\tReset the shopping cart");
        System.out.println("cr,IDX,NR\t\t\t\tRemove NR of products with index IDX from the cart");
        System.out.println("q\t\t\t\t\t\tQuit application");
        System.out.println("-------------------------");
    }

    private void printHistoryUsage() {
        System.out.println("-------------------------");
        System.out.println("hi\t\t\t\t\t\tShow purchase history");
        System.out.println("hi10\t\t\t\t\tShow last 10 purchases");
        System.out.println("hib,SD,ED\t\t\t\tShow purchase history between start date SD and end date ED (yyyy-MM-dd).");
        System.out.println("q\t\t\t\t\t\tQuit application");
        System.out.println("-------------------------");
    }

    private void printUsage2(){
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\t\t\t\t\tShow this help");
        System.out.println("w\t\t\t\t\t\tShow warehouse actions");
        System.out.println("c\t\t\t\t\t\tShow cart actions");
        System.out.println("hi\t\t\t\t\t\tShow history actions");
        System.out.println("t\t\t\t\t\t\tSee team information");
        System.out.println("b\t\t\t\t\t\tShow main menu");
        System.out.println("q\t\t\t\t\t\tQuit application");
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
        System.out.println("cr,IDX,NR\t\t\t\tRemove NR of products with index IDX from the cart");
        System.out.println("hi\t\t\t\t\t\tShow purchase history");
        System.out.println("hi10\t\t\t\t\tShow last 10 purchases");
        System.out.println("hib,SD,ED\t\t\t\tShow purchase history between start date SD and end date ED (yyyy-MM-dd).");
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
        String[] c = command.split(",");
        for (int i = 0; i < c.length; i++) {
            if (i == 0) {
                c[i] = c[i].trim().toLowerCase();
            } else
                c[i] = c[i].trim();
        }
        switch (c[0]) {
            case "h":
                printUsage();
                break;
            case "q":
                log.info("Salesystem CLI shutdown.");
                System.exit(0);
            case "w":
                showStock();
                break;
            case "hi":
                System.out.println("-------------------------");
                history.showAllPurchasesCLI();
                System.out.println("-------------------------");
                break;
            case "hi10":
                System.out.println("-------------------------");
                history.showLastTenPurchasesCLI();
                System.out.println("-------------------------");
                break;
            case "hib":
                if (checkDates(c)) {
                    System.out.println("-------------------------");
                    history.showPurchaseHistoryBetweenDatesCLI(LocalDate.parse(c[1]), LocalDate.parse(c[2]));
                    System.out.println("-------------------------");
                }
                break;
            case "t":
                showTeamInfo();
                break;
            case "c":
                showCart();
                break;
            case "p":
                System.out.println("-------------------------");
                System.out.println("Are you sure that you want to submit current purchase? (y/n)");
                Scanner choice = new Scanner(System.in);
                String input = choice.nextLine().toLowerCase();
                if (input.equals("y")) {
                    cart.submitCurrentPurchase();
                }
                System.out.println("-------------------------");
                break;
            case "r":
                System.out.println("-------------------------");
                cart.cancelCurrentPurchaseCLI();
                log.info("Cart has been reset.");
                System.out.println("-------------------------");
                break;
            case "cr":
                if (checkCommands(c)) {
                    removeItemFromCart(c);
                }
                break;
            case "cp":
                if (checkCommands(c))
                    changePrice(c);
                break;
            case "a":
                if (checkCommands(c))
                    addCart(c);
                break;
            case "wa":
                if (checkCommands(c))
                    addExistingItemToWarehouse(c);
                break;
            case "wan":
                if (checkCommands2(c)) {
                    addNewItemToWarehouse(c);
                    log.debug("User entered index " + c[1] + ", quantity " + c[2] + ", price " + c[3] + ", decription " + c[4] + " and name " + c[5]);
                }
                break;
            case "wr":
                if (checkCommands(c))
                    removeItemFromWarehouse(c);
                break;
            case"b":
                printUsage2();
                break;
            default:
                System.out.println("unknown command");
                break;
        }
    }
}

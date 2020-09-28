package ee.ut.math.tvt.salessystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Transaction {
    private LocalDate localDate;
    private LocalTime localTime;
    private BigDecimal totalQuantity;
    private List<Purchase> purchases;
}
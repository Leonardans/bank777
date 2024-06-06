package first_project;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(String transactionID, int accountID, Enum<TransactionType> transactionType, BigDecimal amount,
                          String description, LocalDate date) {

    @Override
    public String toString() {
        return amount + "$ " + description + ", " + date;
    }
}

package first_project;

import java.time.LocalDate;

public record Transaction(int transactionID, int accountID, Enum<TransactionType> transactionType, Double amount,
                          String description, LocalDate date) {

    @Override
    public String toString() {
        return description + date;
    }
}

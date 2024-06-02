package first.project;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private int toAccountID;
    private int fromAccountID;
    private String transactionType;
    private Double amount;
    private String description;
    private LocalDate date;

    public Transaction(int transactionID, int toAccountID, int fromAccountID, String transactionType,
                        Double amount, String description, LocalDate date) {
        this.transactionID = transactionID;
        this.toAccountID = toAccountID;
        this.fromAccountID = fromAccountID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    public int getToAccountID() {
        return toAccountID;
    }

    public void setToAccountID(int toAccountID) {
        this.toAccountID = toAccountID;
    }
    public int getFromAccountID() {
        return fromAccountID;
    }

    public void setFromAccountID(int fromAccountID) {
        this.fromAccountID = fromAccountID;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return description + date;

    }   
}

package first.project;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private int accountID;
    private String transactionType;
    private Integer amount;
    private String description;
    private LocalDate date;

    public Transaction(int transactionID, int accountID, String transactionType, 
    Integer amount, String description, LocalDate date) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
    }

    public int getTransactionID() {
        return transactionID;
    }
    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }
    public int getAccountID() {
        return accountID;
    }
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
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
        return "Transaction [transactionID=" + transactionID + ", accountID=" + accountID + ", transactionType="
                + transactionType + ", amount=" + amount + ", date=" + date + "]";
    }
}

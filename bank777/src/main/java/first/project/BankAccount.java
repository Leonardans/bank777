package first.project;

import java.util.List;

public class BankAccount {
    private int accountID;
    private int userID;
    private int accountNumber;
    private Integer balance;
    private List<Transaction> transactionHistory;

    public BankAccount(int accountID, int accountNumber, Integer balance, int userID) {
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.userID = userID;
    }

    public int getAccountID() {
        return accountID;
    }
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
    public int getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }
    public Integer getBalance() {
        return balance;
    }
    public void setBalance(Integer balance) {
        this.balance = balance;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }


    @Override
    public String toString() {
        return "BankAccount: [accountID=" + accountID + ", accountNumber=" + accountNumber + ", balance=" + balance
                + ", userID=" + userID + "]";
    }
    
    
}

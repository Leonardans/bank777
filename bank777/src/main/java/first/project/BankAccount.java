package first.project;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import first.project.DAO.*;

public class BankAccount {
    private final int accountID;
    private final int userID;
    private final String accountNumber;
    private Double balance;
    private final List<Transaction> transactionHistory;
    private final Bank bank;
    private TransactionDAO transDAO;
    private AccountDAO accDAO;

    public BankAccount(int accountID, String accountNumber, Double balance, int userID, Bank bank) {
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.userID = userID;
        transactionHistory = new ArrayList<>();
        this.bank = bank;
        transDAO = new TransactionDAO();
        accDAO = new AccountDAO();
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public Double getBalance() {
        return balance;
    }
    public void changeBalance(Double sum, String mark) {
        switch (mark) {
            case "+" -> this.balance += sum;
            case "-" -> this.balance -= sum;
        }
    }
    public int getUserID() {
        return userID;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    public void showTransactionsHistory() {
        for(Transaction transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }
    
    public boolean correctAccount(String provided) {
        Pattern accountPattern = Pattern.compile("^E\\d{13}$");
        Matcher accountMatcher = accountPattern.matcher(provided);
        return accountMatcher.matches();
    }
    public boolean correctSum(String provided) {
        Pattern sumPattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher sumMatcher = sumPattern.matcher(provided);
        return sumMatcher.matches();
    }

    public void deposit(double amount) throws SQLException {
        int transID = bank.generateTransactionID();
        addTransaction(new Transaction(transID, this.accountID, "Deposit", amount,
            "Deposit",LocalDate.now()));
        bank.setMoney(amount);
        changeBalance(amount, "+");

        transDAO.addTransaction(this.accountID, "Deposit", amount, "Deposit", LocalDate.now());
        accDAO.updateAccountBalance(this.accountID, this.balance);
    }

    public boolean withdrawal(BankAccount toAccount, double amount) throws SQLException {
        if(this.balance < amount + 1) {
            return false;
        }
        int transactionID = bank.generateTransactionID();
        addTransaction(new Transaction(transactionID, this.accountID, "Withdrawal", amount,
            "Withdrawal to " + this.getAccountNumber(), LocalDate.now()));
        addTransaction(new Transaction(transactionID, toAccount.accountID, "Deposit", amount,
            "Deposit from" + toAccount.getAccountNumber(), LocalDate.now()));

        this.changeBalance(amount + 1, "-");
        toAccount.changeBalance(amount, "+");
        bank.plusFee(1D);
      

        transDAO.addTransaction(this.accountID, "Withdrawal", amount, "Withdrawal to " + this.getAccountNumber(), LocalDate.now());
        transDAO.addTransaction(toAccount.getAccountID(), "Deposit", amount, "Deposit from " + toAccount.getAccountNumber(), LocalDate.now());
    
        accDAO.updateAccountBalance(this.accountID, this.balance);
        accDAO.updateAccountBalance(toAccount.getAccountID(), toAccount.getBalance());
    
        return true;
    }

    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }

}

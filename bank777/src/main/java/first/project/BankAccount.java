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
        List<Transaction> transactionHistoryFromDatabase = transDAO.getTransactionsByAccountID(this.accountID);

        if(transactionHistoryFromDatabase.size() == 0) {
            for(Transaction transaction : transactionHistory) {
                System.out.println(transaction);
            }
        } else {
            for(Transaction transaction : transactionHistoryFromDatabase) {
                System.out.println(transaction);
            }
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

    public boolean deposit(double amount) {
        int transID = bank.generateTransactionID();
       
        try {
            transDAO.addTransaction(transID, this.accountID, "Deposit", amount, "Deposit", LocalDate.now());
            accDAO.updateAccountBalance(this.accountID, amount);
        } catch (SQLException e) {
            System.out.println("An error occurred during the withdrawal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }  

        addTransaction(new Transaction(transID, this.accountID, "Deposit", amount,
        "Deposit",LocalDate.now()));
        
        bank.setMoney(amount);
        changeBalance(amount, "+");

        return true;
    }

    public boolean withdrawal(BankAccount toAccount, double amount) {
        if(this.balance < amount + 1) {
            return false;
        }
        int transactionID = bank.generateTransactionID();
        
        try {
            transDAO.addTransaction(transactionID, this.accountID, "Withdrawal", amount, "Withdrawal to " + this.getAccountNumber(), LocalDate.now());
            transDAO.addTransaction(transactionID, toAccount.getAccountID(), "Deposit", amount, "Deposit from " + toAccount.getAccountNumber(), LocalDate.now());
        
            accDAO.updateAccountBalance(this.accountID, amount);
            accDAO.updateAccountBalance(toAccount.getAccountID(), amount);
        } catch (SQLException e) {
            System.out.println("An error occurred during the withdrawal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        addTransaction(new Transaction(transactionID, this.accountID, "Withdrawal", amount,
        "Withdrawal",LocalDate.now()));
        addTransaction(new Transaction(transactionID, toAccount.getAccountID(), "Deposit", amount,
        "Deposit",LocalDate.now()));

        this.changeBalance(amount + 1, "-");
        bank.plusFee(1D);
        toAccount.changeBalance(amount, "+");
    
        return true;
    }

    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }

}

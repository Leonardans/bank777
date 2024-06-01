package first.project;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import first.project.DAO.*;

public class BankAccount {
    private final int accountID;
    private final int userID;
    private final String accountNumber;
    private Double balance;
    private TransactionDAO transDAO;
    private AccountDAO accDAO;
    private Bank bank;

    public BankAccount(int accountID, String accountNumber, Double balance, int userID){
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.userID = userID;
        transDAO = new TransactionDAO();
        accDAO = new AccountDAO();
        bank = Bank.getInstance();
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
    public void addToBalance(Double sum, String mark) {
        switch (mark) {
            case "+" -> this.balance += sum;
            case "-" -> this.balance -= sum;
        }
    }
    public int getUserID() {
        return userID;
    }

    public List<Transaction> showTransactionsHistory() {
        return transDAO.getTransactionsByAccountID(this.accountID); 
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
       
        transDAO.addTransaction(transID, this.accountID, "Deposit", amount, "Deposit", LocalDate.now());
        accDAO.updateAccountBalance(this.accountID, amount, true);

        if(transDAO.doesTransactionExists(transID)) {
            bank.setTotalMoney(balance);
            BankDAO.updateBankData(amount, 0D);
            addToBalance(balance, "+");
            return true;
        } else 
            return false; 
    }

    public boolean withdrawal(BankAccount toAccount, double amount) {
        if(this.balance < amount + 1) {
            return false;
        }
        int transactionID = bank.generateTransactionID();
        
        transDAO.addTransaction(transactionID, this.accountID, "Withdrawal", amount, "Withdrawal to " + this.getAccountNumber(), LocalDate.now());
        transDAO.addTransaction(transactionID, toAccount.getAccountID(), "Deposit", amount, "Deposit from " + toAccount.getAccountNumber(), LocalDate.now());
    
        accDAO.updateAccountBalance(this.accountID, amount, false);
        accDAO.updateAccountBalance(toAccount.getAccountID(), amount, true);


        if(transDAO.doesTransactionExists(transactionID)) {
            addToBalance(amount + 1, "-");
            bank.plusFee(1D);
            BankDAO.updateBankData(0, 1D);
            toAccount.addToBalance(amount, "+");
            return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }

}

package first.project;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import first.project.DAO.*;

public class BankAccount {
    private final int accountID;
    private final int userID;
    private final String accountNumber;
    private Double balance;
    private final Bank bank;
    private final TransactionDAO transactionDAO;

    public BankAccount(int accountID, int userID, String accountNumber, Double balance){
        this.accountID = accountID;
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        bank = Bank.getInstance();
        transactionDAO = new TransactionDAO();
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
    public int getUserID() {
        return userID;
    }

    public void addToBalance(Double sum, String sign) {
        switch (sign) {
            case "+" -> this.balance += sum;
            case "-" -> this.balance -= sum;
        }
    }

    public List<Transaction> showTransactionsHistory() {
        return transactionDAO.getTransactionsByAccountID(this.accountID);
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
        double tax = TransactionType.DEPOSIT.getBankTax();

        if(transactionDAO.makeDeposit(transID, this.getAccountID(), amount)) {
            bank.addToMoney(amount, "+");
            bank.plusFee(tax);
            addToBalance(amount - tax, "+");
            return true;
        }
        return false;
    }

    public boolean withdrawal(double amount) {
        int transID = bank.generateTransactionID();
        double tax = TransactionType.WITHDRAWAL.getBankTax();

        if(transactionDAO.makeWithdrawal(transID, this.getAccountID(), amount)) {
            bank.addToMoney(amount, "-");
            bank.plusFee(tax);
            addToBalance(amount + tax, "-");
            return true;
        }
        return false;
    }

    public boolean transfer(BankAccount toAccount, double amount) {
        int transID = bank.generateTransactionID();
        double tax = TransactionType.TRANSFER.getBankTax();

        if(transactionDAO.makeTransfer(transID, toAccount.getAccountID(), this.getAccountID(), amount)){
            bank.plusFee(tax);
            addToBalance(amount + tax, "-");
            toAccount.addToBalance(amount, "+");
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }

}

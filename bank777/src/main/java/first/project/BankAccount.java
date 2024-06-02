package first.project;

import java.util.List;
import java.util.Random;
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
    private final AccountDAO accountDAO;

    public BankAccount(int accountID, String accountNumber, Double balance, int userID){
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.userID = userID;
        bank = Bank.getInstance();
        transactionDAO = new TransactionDAO();
        accountDAO = new AccountDAO();
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
    
    public void addToBalance(Double sum, String sign) {
        switch (sign) {
            case "+" -> this.balance += sum;
            case "-" -> this.balance -= sum;
        }
    }
    
    public int getUserID() {
        return userID;
    }
    
    public int generateAccountID() {
        Random random = new Random();
        int accID = random.nextInt(8_999_999) + 1_000_000;

        while(true) {
            boolean check = accountDAO.doesAccountIdExist(accID);
            if(check) accID = random.nextInt(8_999_999) + 1_000_000;
            else break;
        }

        return accID;
    }

    public String generateAccountNumber() {
        Random random = new Random();
        String accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);

        while(true) {
            boolean check = accountDAO.doesAccountNumberExist(accNum);
            if (check) accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);
            else break;
        }

        return accNum;
    }

    public int generateTransactionID() {
        Random random = new Random();
        int transactionID = random.nextInt(89_999_999) + 10_000_000;

        while(true) {
            boolean check = accountDAO.doesAccountIdExist(transactionID);
            if(check) transactionID = random.nextInt(8_999_999) + 1_000_000;
            else break;
        }

        return transactionID;
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

        if(transactionDAO.makeDeposit(transID, this.getAccountID(), amount - tax)) {
            bank.addToMoney(amount - tax, "+");
            bank.plusFee(tax);
            addToBalance(amount - tax, "+");
            return true;
        }
        return false;
    }

    public boolean withdrawal(double amount) {
        int transID = bank.generateTransactionID();
        double tax = TransactionType.WITHDRAWAL.getBankTax();

        if(transactionDAO.makeWithdrawal(transID, this.getAccountID(), amount - tax)) {
            bank.addToMoney(amount - tax, "-");
            bank.plusFee(tax);
            addToBalance(amount - tax, "-");
            return true;
        }
        return false;
    }

    public boolean transfer(BankAccount toAccount, double amount) {
        int transID = bank.generateTransactionID();
        double tax = TransactionType.TRANSFER.getBankTax();

        if(transactionDAO.makeTransfer(transID, toAccount.generateAccountID(), this.getAccountID(), amount - tax)) {
            bank.addToMoney(tax, "-");
            bank.plusFee(tax);
            addToBalance(amount - tax, "-");
            toAccount.addToBalance(amount - tax, "+");
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }

}

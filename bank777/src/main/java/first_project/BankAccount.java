package first_project;

import java.util.List;
import first_project.DAO.TransactionDAO;

public class BankAccount {
    private final int accountID;
    private final int userID;
    private final String accountNumber;
    private Double balance;
    private final Bank bank = Bank.getInstance();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public BankAccount(int accountID, int userID, String accountNumber, Double balance){
        this.accountID = accountID;
        this.userID = userID;
        this.accountNumber = accountNumber;
        this.balance = balance;
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
    public boolean deposit(double amount) {
        double tax = TransactionType.DEPOSIT.getBankTax();

        if(transactionDAO.makeDeposit(this.getAccountID(), amount, tax)) {
            bank.addToMoney(amount, "+");
            bank.plusFee(tax);
            addToBalance(amount - tax, "+");
            return true;
        }
        return false;
    }
    public boolean withdrawal(double amount) {
        double tax = TransactionType.WITHDRAWAL.getBankTax();

        if(transactionDAO.makeWithdrawal(this.getAccountID(), amount, tax)) {
            bank.addToMoney(amount, "-");
            bank.plusFee(tax);
            addToBalance(amount + tax, "-");
            return true;
        }
        return false;
    }
    public boolean transfer(BankAccount toAccount, double amount) {
        double tax = TransactionType.TRANSFER.getBankTax();

        if(transactionDAO.makeTransfer(toAccount.getAccountID(), this.getAccountID(), amount, tax)){
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

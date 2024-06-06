package first_project;

import java.math.BigDecimal;
import java.util.List;
import first_project.DAO.TransactionDAO;

public class BankAccount {
    private final int accountID;
    private final String accountNumber;
    private final BigDecimal balance;
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public BankAccount(int accountID, String accountNumber, BigDecimal balance){
        this.accountID = accountID;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getAccountID() {
        return accountID;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> showTransactionsHistory() {
        return transactionDAO.getTransactionsByAccountID(this.accountID);
    }
    public boolean deposit(BigDecimal amount) {
        BigDecimal tax = TransactionType.DEPOSIT.getBankTax();

        return transactionDAO.makeDeposit(this.getAccountID(), this.getAccountNumber(), amount, tax);
    }
    public boolean withdrawal(BigDecimal amount) {
        BigDecimal tax = TransactionType.WITHDRAWAL.getBankTax();

        if(amount.compareTo(this.getBalance()) > 0) return false;
        return transactionDAO.makeWithdrawal(this.getAccountID(), this.getAccountNumber(), amount, tax);
    }
    public boolean transfer(BankAccount toAccount, BigDecimal amount) {
        BigDecimal tax = TransactionType.TRANSFER.getBankTax();
        String toAccNum = toAccount.getAccountNumber();
        String fromAccNum = this.getAccountNumber();
        int toID = toAccount.getAccountID();
        int fromID = this.getAccountID();

        if(amount.compareTo(this.getBalance()) > 0) return false;
        return transactionDAO.makeTransfer(toID, fromID, toAccNum, fromAccNum, amount, tax);
    }

    @Override
    public String toString() {
        return accountNumber + ", balance = " + balance + "$";
    }
}

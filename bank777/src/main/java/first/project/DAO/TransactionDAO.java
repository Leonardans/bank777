package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import first.project.Transaction;
import first.project.TransactionType;

public class TransactionDAO {
    BankDAO bankDAO = new BankDAO();
    AccountDAO accountDAO = new AccountDAO();

    public List<Transaction> getTransactionsByAccountID(int accountID) {
        List<Transaction> transactions = new ArrayList<>();
        String SELECT_TRANSACTIONS_BY_ACCOUNT_ID = "SELECT * FROM Transaction WHERE FromAccountID = ? OR ToAccountID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSACTIONS_BY_ACCOUNT_ID)) {

            preparedStatement.setInt(1, accountID);
            preparedStatement.setInt(2, accountID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int transactionID = resultSet.getInt("TransactionID");
                int toAccountID = resultSet.getInt("ToAccountID");
                int fromAccountID = resultSet.getInt("FromAccountID");
                String transactionType = resultSet.getString("TransactionType");
                double amount = resultSet.getDouble("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                transactions.add(new Transaction(transactionID, toAccountID, fromAccountID, transactionType, amount, description, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public boolean makeTransfer(int transactionID, int toAccount, int fromAccount, double amount) {
        String transferSql = "INSERT INTO Transaction (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, FeeAmount, TransactionType, Description) VALUES (?, ?, ?, ?)";

        boolean success = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement transferStmt = connection.prepareStatement(transferSql);
                 PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {

                transferStmt.setInt(1, transactionID);
                transferStmt.setInt(2, toAccount);
                transferStmt.setInt(3, fromAccount);
                transferStmt.setString(4, TransactionType.TRANSFER.name());
                transferStmt.setDouble(5, amount);
                transferStmt.setString(6, "Transfer from " + fromAccount + " to " + toAccount);
                transferStmt.executeUpdate();

                double fee = TransactionType.TRANSFER.getBankTax();
                feeStmt.setInt(1, transactionID);
                feeStmt.setDouble(2, fee);
                feeStmt.setString(3, TransactionType.TRANSFER.name());
                feeStmt.setString(4, "Bank fee for transfer transaction " + transactionID);
                feeStmt.executeUpdate();

                bankDAO.updateBankFee(fee);
                accountDAO.updateAccountBalance(fromAccount, amount + fee, false);
                accountDAO.updateAccountBalance(toAccount, amount, true);

                connection.commit();
                success = true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean makeDeposit(int transactionID, int toAccount, double amount) {
        String depositSql = "INSERT INTO Transaction (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, FeeAmount, TransactionType, Description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement depositStmt = connection.prepareStatement(depositSql)) {
                depositStmt.setInt(1, transactionID);
                depositStmt.setInt(2, toAccount);
                depositStmt.setInt(3, 1);
                depositStmt.setString(4, TransactionType.DEPOSIT.name());
                depositStmt.setDouble(5, amount);
                depositStmt.setString(6, "Deposit to " + toAccount);
                depositStmt.executeUpdate();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }

            double fee = TransactionType.DEPOSIT.getBankTax();
            try (PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {
                feeStmt.setInt(1, transactionID);
                feeStmt.setDouble(2, fee);
                feeStmt.setString(3, TransactionType.DEPOSIT.name());
                feeStmt.setString(4, "Bank fee for deposit transaction " + transactionID);
                feeStmt.executeUpdate();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }

            bankDAO.updateBankData(amount, fee, true);
            accountDAO.updateAccountBalance(toAccount, amount - fee, true);

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean makeWithdrawal(int transactionID, int fromAccount, double amount) {
        String withdrawalSql = "INSERT INTO Transaction (TransactionID, ToAccount, FromAccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, FeeAmount, TransactionType, Description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement withdrawalStmt = connection.prepareStatement(withdrawalSql)) {
                withdrawalStmt.setInt(1, transactionID);
                withdrawalStmt.setInt(2, 1);
                withdrawalStmt.setInt(3, fromAccount);
                withdrawalStmt.setString(4, TransactionType.WITHDRAWAL.name());
                withdrawalStmt.setDouble(5, amount);
                withdrawalStmt.setString(6, "Withdrawal from " + fromAccount);
                withdrawalStmt.executeUpdate();

                double fee = TransactionType.WITHDRAWAL.getBankTax();
                try (PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {
                    feeStmt.setInt(1, transactionID);
                    feeStmt.setDouble(2, fee);
                    withdrawalStmt.setString(3, TransactionType.WITHDRAWAL.name());
                    feeStmt.setString(4, "Bank fee for withdrawal transaction " + transactionID);
                    feeStmt.executeUpdate();
                }

                bankDAO.updateBankData(amount, fee, false);
                accountDAO.updateAccountBalance(fromAccount, amount + fee, false);

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean doesTransactionExists(int transactionID) {
        String selectTransactionSQL = "SELECT COUNT(*) FROM Transaction WHERE TransactionID = ?";
        boolean exists = false;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectTransactionSQL)) {

            preparedStatement.setInt(1, transactionID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                exists = count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }
}

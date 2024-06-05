package first_project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import first_project.Transaction;
import first_project.TransactionType;
import first_project.utils.IDGenerator;

public class TransactionDAO {
    BankDAO bankDAO = new BankDAO();
    AccountDAO accountDAO = new AccountDAO();

    public List<Transaction> getTransactionsByAccountID(int accountID) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {

            transactions.addAll(selectFromDeposit(connection, accountID));
            transactions.addAll(selectFromWithdrawal(connection, accountID));
            transactions.addAll(selectFromTransfer(connection, accountID));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
    public List<Transaction> selectFromDeposit(Connection connection, int accountID) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String selectDeposits = "SELECT * FROM Deposit WHERE AccountID = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(selectDeposits)) {
            preparedStatement.setInt(1, accountID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("TransactionID");
                accountID = resultSet.getInt("AccountID");
                Enum<TransactionType> transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                double amount = resultSet.getDouble("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                transactions.add(new Transaction(transactionID, accountID, transactionType, amount, description, date));
            }
        }
        return  transactions;
    }
    public List<Transaction> selectFromWithdrawal(Connection connection, int accountID) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String selectWithdrawals = "SELECT * FROM Withdrawal WHERE AccountID = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(selectWithdrawals)) {
            preparedStatement.setInt(1, accountID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("TransactionID");
                accountID = resultSet.getInt("AccountID");
                Enum<TransactionType> transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                double amount = resultSet.getDouble("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                transactions.add(new Transaction(transactionID, accountID, transactionType, amount, description, date));
            }
        }
        return  transactions;
    }
    public List<Transaction> selectFromTransfer(Connection connection, int accountID) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String selectTransfers = "SELECT * FROM Transfer WHERE FromAccountID = ? OR ToAccountID = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(selectTransfers)) {
            preparedStatement.setInt(1, accountID);
            preparedStatement.setInt(2, accountID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int transactionID = resultSet.getInt("TransactionID");
                int toAccountID = resultSet.getInt("ToAccountID");
                int fromAccountID = resultSet.getInt("FromAccountID");
                Enum<TransactionType> transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                double amount = resultSet.getDouble("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                accountID = accountID == toAccountID ? toAccountID : fromAccountID;
                transactions.add(new Transaction(transactionID, accountID, transactionType, amount, description, date));
            }
        }
        return  transactions;
    }

    public boolean makeTransfer(int toAccount, int fromAccount, double amount, double tax) {
        String transferSql = "INSERT INTO Transfer (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, FeeAmount, TransactionType, Description) VALUES (?, ?, ?, ?)";

        boolean success = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String transactionID = IDGenerator.generateTransactionID(connection, this);

            try (PreparedStatement transferStmt = connection.prepareStatement(transferSql);
                 PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {

                transferStmt.setString(1, transactionID);
                transferStmt.setInt(2, toAccount);
                transferStmt.setInt(3, fromAccount);
                transferStmt.setString(4, TransactionType.TRANSFER.name());
                transferStmt.setDouble(5, amount);
                transferStmt.setString(6, "Transfer from " + fromAccount + " to " + toAccount);
                transferStmt.executeUpdate();

                feeStmt.setString(1, transactionID);
                feeStmt.setDouble(2, tax);
                feeStmt.setString(3, TransactionType.TRANSFER.name());
                feeStmt.setString(4, "Bank fee for transfer transaction " + transactionID);
                feeStmt.executeUpdate();

                bankDAO.updateBankFee(tax);
                accountDAO.updateAccountBalance(fromAccount, amount + tax, false);
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
    public boolean makeDeposit(int accountID, double amount, double tax) {
        String depositSql = "INSERT INTO Deposit (TransactionID, AccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, Fee, TransactionType, Description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String transactionID = IDGenerator.generateTransactionID(connection, this);

            try (PreparedStatement depositStmt = connection.prepareStatement(depositSql);
                 PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {

                depositStmt.setString(1, transactionID);
                depositStmt.setInt(2, accountID);
                depositStmt.setString(3, TransactionType.DEPOSIT.name());
                depositStmt.setDouble(4, amount);
                depositStmt.setString(5, "Deposit to " + accountID);
                depositStmt.executeUpdate();

                feeStmt.setString(1, transactionID);
                feeStmt.setDouble(2, tax);
                feeStmt.setString(3, TransactionType.DEPOSIT.name());
                feeStmt.setString(4, "Bank fee for deposit transaction " + transactionID);
                feeStmt.executeUpdate();

                bankDAO.updateBankData(amount, tax, true);
                accountDAO.updateAccountBalance(accountID, amount - tax, true);

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
    public boolean makeWithdrawal(int accountID, double amount, double tax) {
        String withdrawalSql = "INSERT INTO Withdrawal (TransactionID, AccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?)";
        String feeSql = "INSERT INTO BankFee (TransactionID, Fee, TransactionType, Description) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String transactionID = IDGenerator.generateTransactionID(connection, this);

            try (PreparedStatement withdrawalStmt = connection.prepareStatement(withdrawalSql);
                 PreparedStatement feeStmt = connection.prepareStatement(feeSql)){

                withdrawalStmt.setString(1, transactionID);
                withdrawalStmt.setInt(2, accountID);
                withdrawalStmt.setString(3, TransactionType.WITHDRAWAL.name());
                withdrawalStmt.setDouble(4, amount);
                withdrawalStmt.setString(5, "Withdrawal from " + accountID);
                withdrawalStmt.executeUpdate();

                feeStmt.setString(1, transactionID);
                feeStmt.setDouble(2, tax);
                feeStmt.setString(3, TransactionType.WITHDRAWAL.name());
                feeStmt.setString(4, "Bank fee for withdrawal transaction " + transactionID);
                feeStmt.executeUpdate();


                bankDAO.updateBankData(amount, tax, false);
                accountDAO.updateAccountBalance(accountID, amount + tax, false);

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
    public boolean doesTransactionExists(Connection connection, String transactionID) {
        String selectTransactionSQL = "SELECT COUNT(*) FROM Transaction WHERE TransactionID = ?";
        boolean exists = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTransactionSQL)) {
            preparedStatement.setString(1, transactionID);
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

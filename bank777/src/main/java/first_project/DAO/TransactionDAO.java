package first_project.DAO;

import java.math.BigDecimal;
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
                String transactionID = resultSet.getString("TransactionID");
                accountID = resultSet.getInt("AccountID");
                Enum<TransactionType> transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                BigDecimal amount = resultSet.getBigDecimal("Amount");
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
                String transactionID = resultSet.getString("TransactionID");
                accountID = resultSet.getInt("AccountID");
                Enum<TransactionType> transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                BigDecimal amount = resultSet.getBigDecimal("Amount");
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
                String transactionID = resultSet.getString("TransactionID");
                Enum<TransactionType> transactionType;
                try {
                    transactionType = TransactionType.valueOf(resultSet.getString("TransactionType"));
                } catch (IllegalArgumentException e) {
                    transactionType = TransactionType.valueOf("UNKNOWN");
                }
                BigDecimal amount = resultSet.getBigDecimal("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                transactions.add(new Transaction(transactionID, accountID, transactionType, amount, description, date));
            }
        }
        return  transactions;
    }

    public boolean makeTransfer(int toID, int fromID, String toAccNum, String fromAccNum, BigDecimal amount, BigDecimal tax) {
        boolean success = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String feeID = IDGenerator.generateFeeID(connection, this);
            String transactionID = IDGenerator.generateTransactionID(feeID);
            LocalDate date = LocalDate.now();

            try {
                bankDAO.insertBankFee(connection, transactionID, feeID, tax, TransactionType.TRANSFER, date);
                insertTransfer(connection, transactionID, toID, fromID, toAccNum, fromAccNum, amount, feeID, date);

                bankDAO.updateTotalFee(connection, tax);
                accountDAO.updateAccountBalance(connection, fromID, amount.add(tax), false);
                accountDAO.updateAccountBalance(connection, toID, amount, true);

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
    private void insertTransfer(Connection connection, String transactionID, int toID, int fromID, String toAccNum,
                                String fromAccNum, BigDecimal amount, String feeID, LocalDate date) throws SQLException {
        String transferSql = "INSERT INTO Transfer (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, FeeID, Description, Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement transferStmt = connection.prepareStatement(transferSql)) {
            transferStmt.setString(1, transactionID);
            transferStmt.setInt(2, toID);
            transferStmt.setInt(3, fromID);
            transferStmt.setString(4, TransactionType.TRANSFER.name());
            transferStmt.setBigDecimal(5, amount);
            transferStmt.setString(6, feeID);
            transferStmt.setString(7, "Transfer from " + fromAccNum + " to " + toAccNum);
            transferStmt.setDate(8, java.sql.Date.valueOf(date));
            transferStmt.executeUpdate();
        }
    }

    public boolean makeDeposit(int accountID, String accNum, BigDecimal amount, BigDecimal tax) {
        boolean success = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String feeID = IDGenerator.generateFeeID(connection, this);
            String transactionID = IDGenerator.generateTransactionID(feeID);
            LocalDate date = LocalDate.now();

            try {
                bankDAO.insertBankFee(connection, transactionID, feeID, tax, TransactionType.DEPOSIT, date);
                insertDeposit(connection, transactionID, accountID, accNum, amount, feeID, date);

                bankDAO.updateBankData(connection, amount, tax, true);
                accountDAO.updateAccountBalance(connection, accountID, amount.subtract(tax), true);

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
    private void insertDeposit(Connection connection, String transactionID, int accountID, String accNum,
                               BigDecimal amount, String feeID, LocalDate date) throws SQLException {
        String depositSql = "INSERT INTO Deposit (TransactionID, AccountID, TransactionType, Amount, FeeID, Description, Date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement depositStmt = connection.prepareStatement(depositSql)) {
            depositStmt.setString(1, transactionID);
            depositStmt.setInt(2, accountID);
            depositStmt.setString(3, TransactionType.DEPOSIT.name());
            depositStmt.setBigDecimal(4, amount);
            depositStmt.setString(5, feeID);
            depositStmt.setString(6, "Deposit to " + accNum);
            depositStmt.setDate(7, java.sql.Date.valueOf(date));
            depositStmt.executeUpdate();
        }
    }

    public boolean makeWithdrawal(int accountID, String accNum, BigDecimal amount, BigDecimal tax) {
        boolean success = false;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            String feeID = IDGenerator.generateFeeID(connection, this);
            String transactionID = IDGenerator.generateTransactionID(feeID);
            LocalDate date = LocalDate.now();

            try {
                bankDAO.insertBankFee(connection, transactionID, feeID, tax, TransactionType.WITHDRAWAL, date);
                insertWithdrawal(connection, transactionID, accountID, accNum, amount, feeID, date);

                bankDAO.updateBankData(connection, amount, tax, false);
                accountDAO.updateAccountBalance(connection, accountID, amount.subtract(tax), false);

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
    private void insertWithdrawal(Connection connection, String transactionID, int accountID,String accNum,
                                  BigDecimal amount, String feeID, LocalDate date) throws SQLException {
        String withdrawalSql = "INSERT INTO Withdrawal (TransactionID, AccountID, TransactionType, Amount, FeeID, Description, Date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement withdrawalStmt = connection.prepareStatement(withdrawalSql)) {
            withdrawalStmt.setString(1, transactionID);
            withdrawalStmt.setInt(2, accountID);
            withdrawalStmt.setString(3, TransactionType.WITHDRAWAL.name());
            withdrawalStmt.setBigDecimal(4, amount);
            withdrawalStmt.setString(5, feeID);
            withdrawalStmt.setString(6, "Withdrawal from " + accNum);
            withdrawalStmt.setDate(7, java.sql.Date.valueOf(date));
            withdrawalStmt.executeUpdate();
        }
    }

    public boolean doesTransactionExists(Connection connection, String feeID) {
        String selectTransactionSQL = "SELECT COUNT(*) FROM BankFee WHERE FeeID = ?";
        boolean exists = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectTransactionSQL)) {
            preparedStatement.setString(1, feeID);
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

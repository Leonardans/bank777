package first_project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import first_project.BankAccount;
import first_project.User;
import first_project.utils.IDGenerator;

public class AccountDAO {

    private final BankDAO bankDAO = new BankDAO();
    public boolean addAccount(User user) {
        int accountID;
        String accountNumber;
        int userID = user.getUserID();

        String insertAccountSQL = "INSERT INTO Account (AccountID, UserID, AccountNumber, Balance) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertAccountSQL)) {

            accountID = IDGenerator.generateAccountID(connection, this);
            accountNumber = IDGenerator.generateAccountNumber(connection, this);
            preparedStatement.setInt(1, accountID);
            preparedStatement.setInt(2, userID);
            preparedStatement.setString(3, accountNumber);
            preparedStatement.setDouble(4, 0D);
            int rowsAffected = preparedStatement.executeUpdate();

            if(rowsAffected > 0) {
                bankDAO.updateTotalAccounts(connection);
                BankAccount newAccount = new BankAccount(accountID, userID, accountNumber, 0D);
                return user.plusOne(newAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void updateAccountBalance(int accountId, double changeAmount, boolean increase) {
        String getBalanceSQL = "SELECT Balance FROM Account WHERE AccountID = ?";
        String updateBalanceSQL = "UPDATE Account SET Balance = ? WHERE AccountID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement getStatement = connection.prepareStatement(getBalanceSQL);
            PreparedStatement updateStatement = connection.prepareStatement(updateBalanceSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("Balance");
                double updatedBalance;
                if (increase) {
                    updatedBalance = currentBalance + changeAmount;
                } else {
                    updatedBalance = currentBalance - changeAmount;
                }

                updateStatement.setDouble(1, updatedBalance);
                updateStatement.setInt(2, accountId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public BankAccount getAccountByAccountNumber(String accountNumber) {
        String selectAccountSQL = "SELECT * FROM Account WHERE AccountNumber = ?";
        BankAccount account = null;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectAccountSQL)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int accountID = resultSet.getInt("AccountID");
                double balance = resultSet.getDouble("Balance");
                int userID = resultSet.getInt("UserID");
                account = new BankAccount(accountID, userID, accountNumber, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }
    public boolean doesAccountNumberExist(String accountNumber) {
        String selectAccountSQL = "SELECT COUNT(*) FROM Account WHERE AccountNumber = ?";
        boolean exists = false;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectAccountSQL)) {
            preparedStatement.setString(1, accountNumber);
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
    public boolean doesAccountNumberExist(Connection connection, String accountNumber) {
        String selectAccountSQL = "SELECT COUNT(*) FROM Account WHERE AccountNumber = ?";
        boolean exists = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectAccountSQL)) {
            preparedStatement.setString(1, accountNumber);
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
    public boolean doesAccountIdExist(Connection connection, int accountId) {
        String selectAccountSQL = "SELECT COUNT(*) FROM Account WHERE AccountID = ?";
        boolean exists = false;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectAccountSQL)) {
            preparedStatement.setInt(1, accountId);
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

package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    public void updateAccountBalance(int accountId, double newBalance) throws SQLException {
        String updateBalanceSQL = "UPDATE Account SET Balance = ? WHERE AccountID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceSQL)) {
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getAccountBalance(int accountId) throws SQLException {
        String selectBalanceSQL = "SELECT Balance FROM Account WHERE AccountID = ?";
        double balance = 0.0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectBalanceSQL)) {
            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("Balance");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }


        return balance;
    }
    public void addAccount(int userId, String accountNumber, double balance) throws SQLException {
        String insertAccountSQL = "INSERT INTO Account (UserID, AccountNumber, Balance) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertAccountSQL)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, accountNumber);
            preparedStatement.setDouble(3, balance);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    


    // Другие методы для работы с аккаунтами (addAccount, deleteAccount и т.д.)

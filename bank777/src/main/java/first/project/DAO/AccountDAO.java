package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

    public class AccountDAO {
        String INSERT_ACCOUNT_SQL = "INSERT INTO Account (accauntId, aserId, accountNumber, Balance) VALUES (?, ?, ?, ?)";
    
        public void addAccount(String accountId, int userId, String accountNumber, double balance) {
            String insertAccountSQL = "INSERT INTO Account (AccountID, UserID, AccountNumber, Balance) VALUES (?, ?, ?, ?)";
        
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertAccountSQL)) {
                
                preparedStatement.setString(1, accountId);
                preparedStatement.setInt(2, userId);
                preparedStatement.setString(3, accountNumber);
                preparedStatement.setDouble(4, balance);
                preparedStatement.executeUpdate();
                System.out.println("Account added successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        public void updateAccountBalance(int accountId, double amount) throws SQLException {
            String getBalanceSQL = "SELECT Balance FROM Account WHERE AccountID = ?";
            String updateBalanceSQL = "UPDATE Account SET Balance = ? WHERE AccountID = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement getStatement = connection.prepareStatement(getBalanceSQL);
                PreparedStatement updateStatement = connection.prepareStatement(updateBalanceSQL)) {

                // Получаем текущий баланс
                getStatement.setInt(1, accountId);
                ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                double currentBalance = resultSet.getDouble("Balance");
                double updatedBalance = currentBalance + amount; // Обновляемый баланс

                // Обновляем баланс в базе данных
                updateStatement.setDouble(1, updatedBalance);
                updateStatement.setInt(2, accountId);
                updateStatement.executeUpdate();
            } else {
                System.out.println("Account not found!");
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }   

    }
    
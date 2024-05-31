package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionDAO {

    public void addTransaction(int accountId, String transactionType, double amount, String description, LocalDate date) {
        String insertTransactionSQL = "INSERT INTO Transaction (AccountID, TransactionType, Amount, Description, Date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.setString(2, transactionType);
            preparedStatement.setDouble(3, amount);
            preparedStatement.setString(4, description);
            preparedStatement.setDate(5, java.sql.Date.valueOf(date));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Другие методы для работы с транзакциями (update, delete, findById и т.д.)
}

package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import first.project.Transaction;

public class TransactionDAO {

    private static final String SELECT_TRANSACTIONS_BY_ACCOUNT_ID = 
        "SELECT * FROM Transaction WHERE AccountID = ?";

    public List<Transaction> getTransactionsByAccountID(int accountID) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_TRANSACTIONS_BY_ACCOUNT_ID)) {
            
            preparedStatement.setInt(1, accountID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int transactionID = resultSet.getInt("TransactionID");
                String transactionType = resultSet.getString("TransactionType");
                double amount = resultSet.getDouble("Amount");
                String description = resultSet.getString("Description");
                LocalDate date = resultSet.getDate("Date").toLocalDate();

                transactions.add(new Transaction(transactionID, accountID, transactionType, amount, description, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void addTransaction(int transactionId, int accountId, String transactionType, double amount, String description, LocalDate date) {
        String insertTransactionSQL = "INSERT INTO Transaction (TransactionID, AccountID, TransactionType, Amount, Description, Date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertTransactionSQL)) {
            preparedStatement.setInt(1, transactionId);
            preparedStatement.setInt(2, accountId);
            preparedStatement.setString(3, transactionType);
            preparedStatement.setDouble(4, amount);
            preparedStatement.setString(5, description);
            preparedStatement.setDate(6, java.sql.Date.valueOf(date));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    
    
    public boolean checkTransactionExistence(int transactionID) {
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
    
    
    // Другие методы для работы с транзакциями (update, delete, findById и т.д.)
}

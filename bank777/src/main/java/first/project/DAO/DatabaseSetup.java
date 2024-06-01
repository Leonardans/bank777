package first.project.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static void setupDatabase() {
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            String createUserTable = "CREATE TABLE IF NOT EXISTS User (" +
                                      "UserID INT AUTO_INCREMENT PRIMARY KEY, " +
                                      "Name VARCHAR(100), " +
                                      "Address VARCHAR(255), " +
                                      "Password VARCHAR(100))";
            statement.executeUpdate(createUserTable);
            String createAccountTable = "CREATE TABLE IF NOT EXISTS Account (" +
                            "AccountID INT PRIMARY KEY, " +
                            "UserID INT, " +
                            "AccountNumber VARCHAR(14), " +
                            "Balance DOUBLE, " +
                            "FOREIGN KEY (UserID) REFERENCES User(UserID))";
            statement.executeUpdate(createAccountTable);

            String createTransactionTable = "CREATE TABLE IF NOT EXISTS Transaction (" +
                                "TransactionID INT AUTO_INCREMENT PRIMARY KEY, " +
                                "AccountID INT, " +
                                "TransactionType VARCHAR(50), " +
                                "Amount DOUBLE, " +
                                "Description VARCHAR(255), " +
                                "Date TIMESTAMP, " +
                                "FOREIGN KEY (AccountID) REFERENCES Account(AccountID))";
            statement.executeUpdate(createTransactionTable);

            for (int i = 1; i <= 10; i++) {
                String insertUser = String.format("INSERT INTO User (Name, Address, Password) VALUES ('User%d', 'Address%d', 'Password%d')", i, i, i);
                statement.executeUpdate(insertUser);
            
                String insertAccount = String.format("INSERT INTO Account (UserID, AccountID, AccountNumber, Balance) VALUES (%d, 'ACC%d', 'AC%d', %f)", i, 1000 + i, 1000 + i, 1000.0 * i);
                statement.executeUpdate(insertAccount);
            
                String insertTransaction = String.format("INSERT INTO Transaction (AccountID, TransactionType, Amount, Date) VALUES ('ACC%d', 'Deposit', %f, NOW())", 1000 + i, 100.0 * i);
                statement.executeUpdate(insertTransaction);
            }
            
            System.out.println("Tables created and initial data inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    public static boolean isTableEmpty(Connection connection, String tableName) {
        String countQuery = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(countQuery)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setupDatabase() {
        try (Connection connection = DatabaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            String createBankTableSQL = "CREATE TABLE IF NOT EXISTS Bank (" +
                                        "BankName VARCHAR(100), " +
                                        "TotalMoney DOUBLE, " +
                                        "BankFee DOUBLE, " +
                                        "TotalUsers INT)";
            statement.executeUpdate(createBankTableSQL);
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

            
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (isTableEmpty(connection, "User")) {
                for (int i = 1; i <= 10; i++) {
                    int userID = 100_000 + i;
                    String name = "User" + i;
                    String address = "Address" + i;
                    String password = "Password" + i;

                    String insertUser = "INSERT INTO User (UserID, Name, Address, Password) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertUser)) {
                        preparedStatement.setInt(1, userID);
                        preparedStatement.setString(2, name);
                        preparedStatement.setString(3, address);
                        preparedStatement.setString(4, password);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("User " + name + " inserted successfully.");
                        }
                    }
                }
            }

            if (isTableEmpty(connection, "Account")) {
                for (int i = 1; i <= 10; i++) {
                    int accID = 1_000_000 + i;
                    String accountNumber = "E" + (1_000_000_000 + i); 
                    double balance = 1000.0 * i;
                    String insertAccount = "INSERT INTO Account (AccountID, AccountNumber, Balance) VALUES (?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertAccount)) {
                        preparedStatement.setInt(1, accID);
                        preparedStatement.setString(2, accountNumber);
                        preparedStatement.setDouble(3, balance);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Account " + accountNumber + " inserted successfully.");
                        }
                    }
                }
            }

            if (isTableEmpty(connection, "Transaction")) {
                for (int i = 1; i <= 10; i++) {
                    int transID = 10_000_000 + i;
                    int accID = 1_000_000 + i;
                    String transactionType = "Deposit";
                    double amount = 100.0 * i;
                    String description = "Description for transaction " + i;
                    String insertTransaction = "INSERT INTO Transaction (TransactionID, AccountID, TransactionType, Amount, Description, Date) VALUES (?, ?, ?, ?, ?, CURDATE())";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransaction)) {
                        preparedStatement.setInt(1, transID);
                        preparedStatement.setInt(2, accID);
                        preparedStatement.setString(3, transactionType);
                        preparedStatement.setDouble(4, amount);
                        preparedStatement.setString(5, description);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Transaction for account " + accID + " inserted successfully.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

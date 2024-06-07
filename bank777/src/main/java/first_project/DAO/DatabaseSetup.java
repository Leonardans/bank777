package first_project.DAO;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import first_project.TransactionType;

public class DatabaseSetup {

    private final static  BankDAO BANKDAO = new BankDAO();

    public static void setupDatabase(String name, BigDecimal money, BigDecimal tax) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            createBankTable(statement);
            createBankFeeTable(statement);
            createUserTable(statement);
            createAccountTable(statement);
            createDepositTable(statement);
            createWithdrawalTable(statement);
            createTransferTable(statement);

            if(isTableEmpty(connection, "Bank")) insertStartBankData(connection, name, money, tax);
            if(isTableEmpty(connection, "User")) insertStartUsersData(connection);
            if(isTableEmpty(connection, "Account")) insertStartAccountsData(connection);
            if(isTableEmpty(connection, "BankFee")) insertStartBankFeeData(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void createBankTable(Statement statement) throws SQLException  {
        String createBankTableSQL = "CREATE TABLE IF NOT EXISTS Bank (" +
                                    "BankID INT PRIMARY KEY, " +
                                    "BankName VARCHAR(100), " +
                                    "TotalMoney DECIMAL(10, 2), " +
                                    "TotalFee DECIMAL(10, 2), " +
                                    "TotalUsers INT, " +
                                    "TotalAccounts INT)";
        statement.executeUpdate(createBankTableSQL);
    }
    public static void createBankFeeTable(Statement statement) throws SQLException {
        String createBankFeeTableSQL = "CREATE TABLE IF NOT EXISTS BankFee (" +
                                        "FeeID VARCHAR(100) PRIMARY KEY, " +
                                        "Fee DECIMAL(10, 2), " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Description VARCHAR(255), " +
                                        "Date DATE)";
        statement.executeUpdate(createBankFeeTableSQL);
    }
    public static void createUserTable(Statement statement) throws SQLException {
        String createUserTable = "CREATE TABLE IF NOT EXISTS User (" +
                                "UserID INT PRIMARY KEY, " +
                                "Name VARCHAR(100), " +
                                "Address VARCHAR(255), " +
                                "Password VARCHAR(100)," +
                                "BankID INT, " +
                                "FOREIGN KEY (BankID) REFERENCES Bank(BankID))";
        statement.executeUpdate(createUserTable);
    }
    public static void createAccountTable(Statement statement) throws SQLException {
        String createAccountTable = "CREATE TABLE IF NOT EXISTS Account (" +
                                    "AccountID INT PRIMARY KEY, " +
                                    "BankID INT, " +
                                    "UserID INT, " +
                                    "AccountNumber VARCHAR(100), " +
                                    "Balance DECIMAL(10, 2), " +
                                    "FOREIGN KEY (BankID) REFERENCES Bank(BankID), " +
                                    "FOREIGN KEY (UserID) REFERENCES User(UserID))";
        statement.executeUpdate(createAccountTable);
    }
    public static void createDepositTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Deposit (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "AccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DECIMAL(10, 2), " +
                                        "FeeID VARCHAR(100), " +
                                        "Description VARCHAR(255), " +
                                        "Date DATE, " +
                                        "FOREIGN KEY (AccountID) REFERENCES Account(AccountID), " +
                                        "FOREIGN KEY (FeeID) REFERENCES BankFee(FeeID))";
        statement.executeUpdate(createTransactionTable);
    }
    public static void createWithdrawalTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Withdrawal (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "AccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DECIMAL(10, 2), " +
                                        "FeeID VARCHAR(100), " +
                                        "Description VARCHAR(255), " +
                                        "Date DATE, " +
                                        "FOREIGN KEY (AccountID) REFERENCES Account(AccountID), " +
                                        "FOREIGN KEY (FeeID) REFERENCES BankFee(FeeID))";
        statement.executeUpdate(createTransactionTable);
    }
    public static void createTransferTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Transfer (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "ToAccountID INT, " +
                                        "FromAccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DECIMAL(10, 2), " +
                                        "FeeID VARCHAR(100), " +
                                        "Description VARCHAR(255), " +
                                        "Date DATE, " +
                                        "FOREIGN KEY (ToAccountID) REFERENCES Account(AccountID), " +
                                        "FOREIGN KEY (FromAccountID) REFERENCES Account(AccountID), " +
                                        "FOREIGN KEY (FeeID) REFERENCES BankFee(FeeID))";
        statement.executeUpdate(createTransactionTable);
    }

    public static void insertStartBankData(Connection connection, String name, BigDecimal money, BigDecimal tax) throws SQLException {
        String insertBankData = "INSERT INTO Bank (BankID, BankName, TotalMoney, TotalFee, TotalUsers, TotalAccounts) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertBankData)) {
            preparedStatement.setInt(1, 777);
            preparedStatement.setString(2, name);
            preparedStatement.setBigDecimal(3, money);
            preparedStatement.setBigDecimal(4, tax);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bank data inserted successfully.");
            }
        }
    }
    public static void insertStartUsersData(Connection connection) throws SQLException {
        for (int i = 1; i <= 10; i++) {
            int userID = 100_000 + i;
            String name = "User" + i;
            String address = "Address " + i + "-" + i+1;
            String password = "Password" + i + "$";

            String insertUser = "INSERT INTO User (UserID, Name, Address, Password, BankID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertUser)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);
                preparedStatement.setString(4, password);
                preparedStatement.setInt(5, 777);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User " + name + " inserted successfully.");
                    BANKDAO.updateTotalUsers(connection);
                }
            }
        }
    }
    public static void insertStartAccountsData(Connection connection) throws SQLException {
        for (int i = 1; i <= 10; i++) {
            int accountID = 1_000_000 + i;
            int userID = 100_000 + i;
            String accountNumber = "E" + (1_000_000_000_000L + i);
            BigDecimal balance = new BigDecimal(10000 * i);
            String insertAccount = "INSERT INTO Account (AccountID, BankID, UserID, AccountNumber, Balance) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAccount)) {
                preparedStatement.setInt(1, accountID);
                preparedStatement.setInt(2, 777);
                preparedStatement.setInt(3, userID);
                preparedStatement.setString(4, accountNumber);
                preparedStatement.setBigDecimal(5, balance);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account " + accountNumber + " inserted successfully.");
                    BANKDAO.updateTotalAccounts(connection);
                }
            }
        }
    }
    public static void insertStartTransfersData(Connection connection, String transactionID, int toAccountID, int fromAccountID,
                                                String transactionType, BigDecimal amount, String feeID, String description, LocalDate date) throws SQLException {
        String insertTransferTo = "INSERT INTO Transfer (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, FeeID, Description, Date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransferTo)) {
            preparedStatement.setString(1, transactionID);
            preparedStatement.setInt(2, toAccountID);
            preparedStatement.setInt(3, fromAccountID);
            preparedStatement.setString(4, transactionType);
            preparedStatement.setBigDecimal(5, amount);
            preparedStatement.setString(6, feeID);
            preparedStatement.setString(7, description);
            preparedStatement.setDate(8, java.sql.Date.valueOf(date));
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transfer from account " + fromAccountID + " to account " + toAccountID + "inserted successfully.");
            }
        }
    }
    public static void insertStartBankFeeData(Connection connection) throws SQLException {
        for (int i = 1, j = 1; i <= 10; i++) {
            String transactionID = "AB" + "10_000_000" + i;
            int toAccountID = i <= 5 ? 1_000_000 + i : 1_000_005 + j++;
            int fromAccountID = i <= 5 ? 1_000_005 + i : 1_000_000 + j;
            String transactionType = "TRANSFER";
            BigDecimal amount = new BigDecimal(100 * i);
            String description = "Description for transfer " + i;
            String feeID = "BA" + "10_000_000" + i;
            LocalDate date = LocalDate.now();

            String insertFeeForTransfer = "INSERT INTO BankFee (FeeID, Fee, TransactionType, Description, Date) VALUES (?, ?, ?, ?, ?)";

            try(PreparedStatement preparedStatement = connection.prepareStatement(insertFeeForTransfer)) {
                preparedStatement.setString(1, feeID);
                preparedStatement.setBigDecimal(2, TransactionType.TRANSFER.getBankTax());
                preparedStatement.setString(3, TransactionType.TRANSFER.name());
                preparedStatement.setString(4, "Bank fee for transfer transaction " + transactionID);
                preparedStatement.setDate(5, java.sql.Date.valueOf(date));
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully.");
                    insertStartTransfersData(connection, transactionID, toAccountID, fromAccountID, transactionType, amount, feeID, description, date);
                }
            }
        }
    }
}

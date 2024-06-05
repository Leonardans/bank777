package first_project.DAO;

import java.sql.*;

import first_project.Bank;
import first_project.TransactionType;

public class DatabaseSetup {
    private final static  Bank BANK = Bank.getInstance();
    private final static  BankDAO BANKDAO = new BankDAO();

    public static void setupDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            createBankTable(statement);
            createUserTable(statement);
            createAccountTable(statement);
            createDepositTable(statement);
            createWithdrawalTable(statement);
            createTransferTable(statement);
            createBankFeeTable(statement);

            if(!isTableEmpty(connection, "Bank")) {
                BANK.addToMoney(BankDAO.getTotalMoney(), "+");
                BANK.plusFee(BankDAO.getBankFee());
            }
            if(isTableEmpty(connection, "Bank")) insertStartBankData(connection);
            if(isTableEmpty(connection, "User")) insertStartUsersData(connection);
            if(isTableEmpty(connection, "Account")) insertStartAccountsData(connection);
            if(isTableEmpty(connection, "Transfer")) insertStartTransfersData(connection);

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
                                    "BankName VARCHAR(100) PRIMARY KEY, " +
                                    "TotalMoney DOUBLE, " +
                                    "TotalFee DOUBLE, " +
                                    "TotalUsers INT, " +
                                    "TotalAccounts INT)";
        statement.executeUpdate(createBankTableSQL);
    }
    public static void createUserTable(Statement statement) throws SQLException {
        String createUserTable = "CREATE TABLE IF NOT EXISTS User (" +
                                "UserID INT PRIMARY KEY, " +
                                "Name VARCHAR(100), " +
                                "Address VARCHAR(255), " +
                                "Password VARCHAR(100))";
        statement.executeUpdate(createUserTable);
    }
    public static void createAccountTable(Statement statement) throws SQLException {
        String createAccountTable = "CREATE TABLE IF NOT EXISTS Account (" +
                                    "AccountID INT PRIMARY KEY, " +
                                    "UserID INT, " +
                                    "AccountNumber VARCHAR(100), " +
                                    "Balance DOUBLE, " +
                                    "FOREIGN KEY (UserID) REFERENCES User(UserID))";
        statement.executeUpdate(createAccountTable);
    }
    public static void createDepositTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Deposit (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "AccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DOUBLE, " +
                                        "Description VARCHAR(255), " +
                                        "Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (AccountID) REFERENCES Account(AccountID))";
        statement.executeUpdate(createTransactionTable);
    }
    public static void createWithdrawalTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Withdrawal (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "AccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DOUBLE, " +
                                        "Description VARCHAR(255), " +
                                        "Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (AccountID) REFERENCES Account(AccountID))";
        statement.executeUpdate(createTransactionTable);
    }
    public static void createTransferTable(Statement statement) throws SQLException {
        String createTransactionTable = "CREATE TABLE IF NOT EXISTS Transfer (" +
                                        "TransactionID VARCHAR(100) PRIMARY KEY, " +
                                        "ToAccountID INT, " +
                                        "FromAccountID INT, " +
                                        "TransactionType VARCHAR(20) NOT NULL, " +
                                        "Amount DOUBLE, " +
                                        "Description VARCHAR(255), " +
                                        "Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "FOREIGN KEY (ToAccountID) REFERENCES Account(AccountID), " +
                                        "FOREIGN KEY (FromAccountID) REFERENCES Account(AccountID))";
        statement.executeUpdate(createTransactionTable);
    }
    public static void createBankFeeTable(Statement statement) throws SQLException {
        String createBankFeeTableSQL = "CREATE TABLE IF NOT EXISTS BankFee (" +
                                        "FeeID INT AUTO_INCREMENT PRIMARY KEY, " +
                                        "TransactionID VARCHAR(100), " +
                                        "Fee DOUBLE, " +
                                        "TransactionType ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER'), " +
                                        "Description VARCHAR(255), " +
                                        "FOREIGN KEY (TransactionID) REFERENCES Deposit (TransactionID), " +
                                        "FOREIGN KEY (TransactionID) REFERENCES Withdrawal (TransactionID), " +
                                        "FOREIGN KEY (TransactionID) REFERENCES Transfer (TransactionID))";
        statement.executeUpdate(createBankFeeTableSQL);
    }

    public static void insertStartBankData(Connection connection) throws SQLException {
        String insertBankData = "INSERT INTO Bank (BankName, TotalMoney, TotalFee, TotalUsers, TotalAccounts) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertBankData)) {
            preparedStatement.setString(1, BANK.getBankName());
            preparedStatement.setDouble(2, BANK.getTotalMoney());
            preparedStatement.setDouble(3, BANK.getBankFee());
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
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

            String insertUser = "INSERT INTO User (UserID, Name, Address, Password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertUser)) {
                preparedStatement.setInt(1, userID);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);
                preparedStatement.setString(4, password);
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
            double balance = 10000.0 * i;
            String insertAccount = "INSERT INTO Account (AccountID, UserID, AccountNumber, Balance) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertAccount)) {
                preparedStatement.setInt(1, accountID);
                preparedStatement.setInt(2, userID);
                preparedStatement.setString(3, accountNumber);
                preparedStatement.setDouble(4, balance);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Account " + accountNumber + " inserted successfully.");
                    BANKDAO.updateTotalAccounts(connection);
                }
            }
        }
    }
    public static void insertStartTransfersData(Connection connection) throws SQLException {
        for (int i = 1, j = 1; i <= 10; i++) {
            String transactionID = "AB" + "10_000_000" + i;
            int toAccountID = i <= 5 ? 1_000_000 + i : 1_000_005 + j++;
            int fromAccountID = i <= 5 ? 1_000_005 + i : 1_000_000 + j;
            String transactionType = "TRANSFER";
            double amount = 100.0 * i;
            String description = "Description for transfer " + i;

            String insertTransferTo = "INSERT INTO Transfer (TransactionID, ToAccountID, FromAccountID, TransactionType, Amount, Description) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertTransferTo)) {
                preparedStatement.setString(1, transactionID);
                preparedStatement.setInt(2, toAccountID);
                preparedStatement.setInt(3, fromAccountID);
                preparedStatement.setString(4, transactionType);
                preparedStatement.setDouble(5, amount);
                preparedStatement.setString(6, description);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transfer for account " + toAccountID + " inserted successfully.");
                    insertStartBankFeeData(connection, transactionID);
                }
            }
        }
    }
    public static void insertStartBankFeeData(Connection connection, String transactionID) throws SQLException {
        String insertFeeForTransfer = "INSERT INTO BankFee (TransactionID, Fee, TransactionType) VALUES (?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(insertFeeForTransfer)) {
            preparedStatement.setString(1, transactionID);
            preparedStatement.setDouble(2, TransactionType.TRANSFER.getBankTax());
            preparedStatement.setString(3, TransactionType.TRANSFER.name());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully.");
            }
        }
    }
}

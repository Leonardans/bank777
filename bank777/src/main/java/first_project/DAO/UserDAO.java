package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import first.project.BankAccount;
import first.project.User;

public class UserDAO {

    public boolean addUser(int id, String name, String address, String password) {
        String insertUserSQL = "INSERT INTO User (UserID, Name, Address, Password) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Autocommit status: " + connection.getAutoCommit());
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, password);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String selectUserSQL = "SELECT * FROM User WHERE UserID = ?";
        User user = null;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("UserID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean checkUserIdExistence(int userId) {
        String selectUserSQL = "SELECT COUNT(*) FROM User WHERE UserID = ?";
        boolean exists = false;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setInt(1, userId);
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

    public List<BankAccount> getUserAccounts(int userID) {
        String selectAccountsSQL = "SELECT * FROM Account WHERE UserID = ?";
        List<BankAccount> userAccounts = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectAccountsSQL)) {
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int accountID = resultSet.getInt("AccountID");
                int userId = resultSet.getInt("UserID");
                String accountNumber = resultSet.getString("AccountNumber");
                double balance = resultSet.getDouble("Balance");
                BankAccount account = new BankAccount(accountID, userID, accountNumber, balance);
                userAccounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userAccounts;
    }

    public User checkUser(int id, String password) {
        String selectUserSQL = "SELECT * FROM User WHERE UserID = ? AND Password = ?";
        User user = null;

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                    resultSet.getInt("UserID"),
                    resultSet.getString("Name"),
                    resultSet.getString("Address"),
                    resultSet.getString("Password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}

package first.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import first.project.User;

public class UserDAO {

    public void addUser(String name, String address, String password) {
        String insertUserSQL = "INSERT INTO Users (Name, Address, Password) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
            System.out.println("User added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public User getUserById(Connection connection, int userId) throws SQLException {
        String selectUserSQL = "SELECT * FROM User WHERE UserID = ?";
        User user = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
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
        }

        return user;
    }

    // Другие методы для работы с пользователями (deleteUser, updateUser и т.д.)
}
package first_project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankDAO {

    public static Double getTotalMoney() {
        String selectTotalMoneySQL = "SELECT TotalMoney FROM Bank";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectTotalMoneySQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("TotalMoney");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0D;
    }
    public static Double getBankFee() {
        String selectBankFeeSQL = "SELECT TotalFee FROM Bank";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectBankFeeSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("TotalFee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0D;
    }

    public void updateTotalUsers(Connection connection) {
        String getTotalUsersSQL = "SELECT TotalUsers FROM Bank";
        String updateTotalUsersSQL = "UPDATE Bank SET TotalUsers = ?";

        try (PreparedStatement getStatement = connection.prepareStatement(getTotalUsersSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateTotalUsersSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if(resultSet.next()) {
                int users = resultSet.getInt("TotalUsers");
                updateStatement.setInt(1, users + 1);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateTotalAccounts(Connection connection) {
        String getTotalAccountsSQL = "SELECT TotalAccounts FROM Bank";
        String updateTotalAccountsSQL = "UPDATE Bank SET TotalAccounts = ?";

        try (PreparedStatement getStatement = connection.prepareStatement(getTotalAccountsSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateTotalAccountsSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if(resultSet.next()) {
                int accounts = resultSet.getInt("TotalAccounts");
                updateStatement.setInt(1, accounts + 1);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateBankFee(double fee){
        String selectBankFeeSql = "SELECT TotalFee FROM Bank";
        String updateBankFeeSql = "UPDATE Bank SET TotalFee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement selectStmt = connection.prepareStatement(selectBankFeeSql);
            ResultSet resultSet = selectStmt.executeQuery()) {
            if (resultSet.next()) {
                double currentBankFee = resultSet.getDouble("TotalFee");
                double updatedBankFee = currentBankFee + fee;

                try (PreparedStatement updateStmt = connection.prepareStatement(updateBankFeeSql)) {
                    updateStmt.setDouble(1, updatedBankFee);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateBankData(double amount, double fee, boolean increase) {
        String getBankDataSQL = "SELECT TotalMoney, TotalFee FROM Bank";
        String updateBankDataSQL = "UPDATE Bank SET TotalMoney = ?, TotalFee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement getStatement = connection.prepareStatement(getBankDataSQL);
            PreparedStatement updateStatement = connection.prepareStatement(updateBankDataSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                double currentMoney = resultSet.getDouble("TotalMoney");
                double currentFee = resultSet.getDouble("TotalFee");

                double updatedMoney = increase ? currentMoney + amount : currentMoney - amount;
                double updatedFee = currentFee + fee;

                updateStatement.setDouble(1, updatedMoney);
                updateStatement.setDouble(2, updatedFee);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


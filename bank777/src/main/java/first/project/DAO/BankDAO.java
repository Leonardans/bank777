package first.project.DAO;

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
        String selectBankFeeSQL = "SELECT BankFee FROM Bank";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectBankFeeSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("BankFee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0D; 
    }
   
    public static boolean insertBankData(String bankName, double totalMoney, double bankFee, int totalUsers) {
        if (!isBankExist(bankName)) {
            String insertBankDataSQL = "INSERT INTO Bank (BankName, TotalMoney, BankFee, TotalUsers) VALUES (?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertBankDataSQL)) {
                preparedStatement.setString(1, bankName);
                preparedStatement.setDouble(2, totalMoney);
                preparedStatement.setDouble(3, bankFee);
                preparedStatement.setInt(4, totalUsers);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                }
            } catch (SQLException e) {
                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    private static boolean isBankExist(String bankName) {
        String selectBankSQL = "SELECT COUNT(*) FROM Bank WHERE BankName = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectBankSQL)) {
            preparedStatement.setString(1, bankName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateBankFee(double fee){
        String selectBankFeeSql = "SELECT BankFee FROM Bank";
        String updateBankFeeSql = "UPDATE Bank SET BankFee = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement selectStmt = connection.prepareStatement(selectBankFeeSql);
            ResultSet resultSet = selectStmt.executeQuery()) {
            if (resultSet.next()) {
                double currentBankFee = resultSet.getDouble("BankFee");
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
    
    public boolean updateBankData(double amount, double fee, boolean increase) {
        String getBankDataSQL = "SELECT TotalMoney, BankFee FROM Bank";
        String updateBankDataSQL = "UPDATE Bank SET TotalMoney = ?, BankFee = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement getStatement = connection.prepareStatement(getBankDataSQL);
            PreparedStatement updateStatement = connection.prepareStatement(updateBankDataSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                double currentMoney = resultSet.getDouble("TotalMoney");
                double currentFee = resultSet.getDouble("BankFee");

                double updatedMoney = increase ? currentMoney + amount : currentMoney - amount;
                double updatedFee = currentFee + fee;

                updateStatement.setDouble(1, updatedMoney);
                updateStatement.setDouble(2, updatedFee);
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) return true;
            } 
            return false;
        } catch (SQLException e) {
            return false;
        }
    }
}


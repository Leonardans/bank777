package first_project.DAO;

import first_project.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BankDAO {

    public static BigDecimal getTotalMoney() {
        String selectTotalMoneySQL = "SELECT TotalMoney FROM Bank";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectTotalMoneySQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("TotalMoney");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }
    public static BigDecimal getBankFee() {
        String selectBankFeeSQL = "SELECT TotalFee FROM Bank";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectBankFeeSQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("TotalFee");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    protected void updateTotalUsers(Connection connection) {
        String getTotalUsersSQL = "SELECT TotalUsers FROM Bank";
        String updateTotalUsersSQL = "UPDATE Bank SET TotalUsers = ?";

        try (PreparedStatement getStatement = connection.prepareStatement(getTotalUsersSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateTotalUsersSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if(resultSet.next()) {
                int users = resultSet.getInt("TotalUsers");
                updateStatement.setInt(1, users + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    protected void updateTotalAccounts(Connection connection) {
        String getTotalAccountsSQL = "SELECT TotalAccounts FROM Bank";
        String updateTotalAccountsSQL = "UPDATE Bank SET TotalAccounts = ?";

        try (PreparedStatement getStatement = connection.prepareStatement(getTotalAccountsSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateTotalAccountsSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if(resultSet.next()) {
                int accounts = resultSet.getInt("TotalAccounts");
                updateStatement.setInt(1, accounts + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    protected void updateTotalFee(Connection connection, BigDecimal fee) {
        String selectBankFeeSql = "SELECT TotalFee FROM Bank";
        String updateBankFeeSql = "UPDATE Bank SET TotalFee = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectBankFeeSql);
             ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal currentBankFee = resultSet.getBigDecimal("TotalFee");
                    BigDecimal updatedBankFee = currentBankFee.add(fee);

                    try (PreparedStatement updateStmt = connection.prepareStatement(updateBankFeeSql)) {
                        updateStmt.setBigDecimal(1, updatedBankFee);
                        updateStmt.executeUpdate();
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    protected void updateBankData(Connection connection, BigDecimal amount, BigDecimal fee, boolean increase) {
        String getBankDataSQL = "SELECT TotalMoney, TotalFee FROM Bank";
        String updateBankDataSQL = "UPDATE Bank SET TotalMoney = ?, TotalFee = ?";

        try (PreparedStatement getStatement = connection.prepareStatement(getBankDataSQL);
             PreparedStatement updateStatement = connection.prepareStatement(updateBankDataSQL)) {

            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                BigDecimal currentMoney = resultSet.getBigDecimal("TotalMoney");
                BigDecimal currentFee = resultSet.getBigDecimal("TotalFee");

                BigDecimal updatedMoney = increase ? currentMoney.add(amount) : currentMoney.subtract(amount);
                BigDecimal updatedFee = currentFee.add(fee);

                updateStatement.setBigDecimal(1, updatedMoney);
                updateStatement.setBigDecimal(2, updatedFee);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void insertBankFee(Connection connection, String transactionID, String feeID, BigDecimal tax, TransactionType type,
                                 LocalDate date) throws SQLException {
        String feeSql = "INSERT INTO BankFee (FeeID, Fee, TransactionType, Description, Date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement feeStmt = connection.prepareStatement(feeSql)) {
            feeStmt.setString(1, feeID);
            feeStmt.setBigDecimal(2, tax);
            feeStmt.setString(3, type.name());
            feeStmt.setString(4, "Bank fee for" + type.name() +  " " + transactionID);
            feeStmt.setDate(5, java.sql.Date.valueOf(date));
            feeStmt.executeUpdate();
        }
    }
}


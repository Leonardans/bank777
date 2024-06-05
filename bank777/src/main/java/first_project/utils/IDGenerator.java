package first_project.utils;

import java.sql.*;
import first_project.DAO.AccountDAO;
import first_project.DAO.TransactionDAO;
import first_project.DAO.UserDAO;

import java.util.Random;

public class IDGenerator {
    private final static Random random = new Random();

    public static int generateUserID(Connection connection, UserDAO userDAO) {
        int userID = random.nextInt(899_999) + 100_000;

        while(true) {
            boolean check = userDAO.checkUserIdExistence(connection, userID);
            if (check) userID = random.nextInt(899_999) + 100_000;
            else break;
        }

        return userID;
    }
    public static int generateAccountID(Connection connection, AccountDAO accountDAO) {
        int accountID = random.nextInt(8_999_999) + 1_000_000;

        while(true) {
            boolean check = accountDAO.doesAccountIdExist(connection, accountID);
            if(check) accountID = random.nextInt(8_999_999) + 1_000_000;
            else break;
        }
        return accountID;
    }
    public static String generateAccountNumber(Connection connection, AccountDAO accountDAO) {
        String accountNumber = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);

        while(true) {
            boolean check = accountDAO.doesAccountNumberExist(connection, accountNumber);
            if (check) accountNumber = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);
            else break;
        }
        return accountNumber;
    }
    public static String generateTransactionID(Connection connection, TransactionDAO transactionDAO) {
        String transactionID = (char) (random.nextInt(26) + 'A') + (char) (random.nextInt(26) + 'A')
            + String.valueOf(random.nextInt(88_999_999) + 10_000_000);

        while(true) {
            boolean check = transactionDAO.doesTransactionExists(connection, transactionID);
            if (check) transactionID = (char) (random.nextInt(26) + 'A') + (char) (random.nextInt(26) + 'A')
                + String.valueOf(random.nextInt(88_999_999) + 10_000_000);
            else break;
        }
        return transactionID;
    }
}

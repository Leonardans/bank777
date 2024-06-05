package first_project.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://sql12.freemysqlhosting.net:3306/sql12711983";
    private static final String USERNAME = "sql12711983";
    private static final String PASSWORD = "P1rwR1ZJcR";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

package first_project.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7712250";
    private static final String USERNAME = "sql7712250";
    private static final String PASSWORD = "xwlRrxil1b";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

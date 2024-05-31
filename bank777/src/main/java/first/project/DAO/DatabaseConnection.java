package first.project.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root";
    private static final String PASSWORD = "password";


    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10); // Максимальное количество соединений в пуле
        config.setMinimumIdle(5); // Минимальное количество неиспользуемых соединений
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        //return DriverManager.getConnection(URL, USER, PASSWORD);
        return dataSource.getConnection();
    }

    public void updateAccountBalance(int accountID, Double balance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAccountBalance'");
    }
}
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // Alterar porta da URI, '5433' é a porta que uso no meu computador. O padrão é '5432'
    private static final String URL = "jdbc:postgresql://localhost:5433/projeto_final";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

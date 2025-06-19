package test;

import java.sql.Connection;
import java.sql.SQLException;

import dao.ConnectionFactory;

public class DBConnectionTest {
    public static void main(String[] args) {
        Connection conn = null;
        try {
            System.out.println("Tentando conectar ao banco de dados...");
            conn = ConnectionFactory.getConnection();
            if (conn != null) {
                System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
            } else {
                System.out.println("Erro: Não foi possível obter uma conexão com o banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro de SQL ao conectar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(conn);
            System.out.println("Tentativa de fechar conexão.");
        }
    }
}

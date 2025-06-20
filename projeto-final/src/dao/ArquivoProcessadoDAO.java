package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArquivoProcessadoDAO {

    public boolean hashJaExiste(String hash) {
        String sql = "SELECT COUNT(*) FROM tb_arquivos_processados WHERE hash_arquivo = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao verificar hash no banco.", e);
        }
        return false;
    }

    public void salvarRegistro(String nomeArquivo, String hash) {
        String sql = "INSERT INTO tb_arquivos_processados (nome_arquivo, hash_arquivo) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeArquivo);
            stmt.setString(2, hash);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar registro de arquivo processado.", e);
        }
    }
}
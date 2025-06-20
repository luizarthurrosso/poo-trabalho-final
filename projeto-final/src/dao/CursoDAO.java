package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Curso;
import model.Fase;

public class CursoDAO {

    public void salvar(Curso curso) {
        String sql = "INSERT INTO tb_cursos (nome_curso, data_processamento, sequencia_arquivo, versao_layout) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, curso.getNomeCurso());
            stmt.setDate(2, Date.valueOf(curso.getDataProcessamento()));
            stmt.setInt(3, curso.getSequenciaArquivo());
            stmt.setString(4, curso.getVersaoLayout());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    curso.setId(rs.getInt(1));
                }
            }

            FaseDAO faseDAO = new FaseDAO();
            for (Fase fase : curso.getFases()) {
                faseDAO.salvar(fase, curso.getId(), conn);
            }

            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar curso completo.", e);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
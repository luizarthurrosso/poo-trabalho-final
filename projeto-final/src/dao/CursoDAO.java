package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void salvarManual(Curso curso) {
        String sql = "INSERT INTO tb_cursos (nome_curso, data_processamento, sequencia_arquivo, versao_layout) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNomeCurso());
            stmt.setDate(2, Date.valueOf(curso.getDataProcessamento()));
            stmt.setInt(3, curso.getSequenciaArquivo());
            stmt.setString(4, curso.getVersaoLayout());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar curso manual.", e);
        }
    }

    public List<Curso> buscarTodos() {
        String sql = "SELECT * FROM tb_cursos ORDER BY nome_curso";
        List<Curso> cursos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setId(rs.getInt("id"));
                curso.setNomeCurso(rs.getString("nome_curso"));
                curso.setDataProcessamento(rs.getDate("data_processamento").toLocalDate());
                curso.setSequenciaArquivo(rs.getInt("sequencia_arquivo"));
                curso.setVersaoLayout(rs.getString("versao_layout"));
                cursos.add(curso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar todos os cursos.", e);
        }
        return cursos;
    }
    
    public void atualizar(Curso curso) {
        String sql = "UPDATE tb_cursos SET nome_curso = ?, data_processamento = ?, sequencia_arquivo = ?, versao_layout = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getNomeCurso());
            stmt.setDate(2, Date.valueOf(curso.getDataProcessamento()));
            stmt.setInt(3, curso.getSequenciaArquivo());
            stmt.setString(4, curso.getVersaoLayout());
            stmt.setInt(5, curso.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar curso.", e);
        }
    }

    public Curso buscarPorId(int id) {
        String sql = "SELECT * FROM tb_cursos WHERE id = ?";
        Curso curso = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    curso = new Curso();
                    curso.setId(rs.getInt("id"));
                    curso.setNomeCurso(rs.getString("nome_curso"));
                    curso.setDataProcessamento(rs.getDate("data_processamento").toLocalDate());
                    curso.setSequenciaArquivo(rs.getInt("sequencia_arquivo"));
                    curso.setVersaoLayout(rs.getString("versao_layout"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar curso por ID.", e);
        }
        return curso;
    }

    public void excluir(int id) {
        String sql = "DELETE FROM tb_cursos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir curso.", e);
        }
    }
}
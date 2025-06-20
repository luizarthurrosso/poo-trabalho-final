package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Professor;

public class ProfessorDAO {

    public void salvar(Professor professor) {
        String sql = "INSERT INTO tb_professores (nome_professor, titulo_docente) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, professor.getNomeProfessor());
            stmt.setInt(2, professor.getTituloDocente());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    professor.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar professor no banco de dados.", e);
        }
    }
    
    public void atualizar(Professor professor) {
        String sql = "UPDATE tb_professores SET nome_professor = ?, titulo_docente = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, professor.getNomeProfessor());
            stmt.setInt(2, professor.getTituloDocente());
            stmt.setInt(3, professor.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar professor.", e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM tb_professores WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir professor.", e);
        }
    }
    
    public Professor buscarPorId(int id) {
        String sql = "SELECT * FROM tb_professores WHERE id = ?";
        Professor professor = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    professor = new Professor();
                    professor.setId(rs.getInt("id"));
                    professor.setNomeProfessor(rs.getString("nome_professor"));
                    professor.setTituloDocente(rs.getInt("titulo_docente"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar professor por ID.", e);
        }
        return professor;
    }

    public List<Professor> buscarTodos() {
        String sql = "SELECT * FROM tb_professores ORDER BY nome_professor";
        List<Professor> professores = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Professor professor = new Professor();
                professor.setId(rs.getInt("id"));
                professor.setNomeProfessor(rs.getString("nome_professor"));
                professor.setTituloDocente(rs.getInt("titulo_docente"));
                professores.add(professor);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os professores.", e);
        }
        return professores;
    }
}
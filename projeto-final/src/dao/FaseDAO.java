package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Disciplina;
import model.Fase;

public class FaseDAO {
    
    public void salvar(Fase fase, int cursoId, Connection conn) throws SQLException {
        String sql = "INSERT INTO tb_fases (nome_fase, curso_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fase.getNomeFase());
            stmt.setInt(2, cursoId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fase.setId(rs.getInt(1));
                }
            }
        }
        DisciplinaDAO disciplinaDAO = new DisciplinaDAO();
        for (Disciplina disciplina : fase.getDisciplinas()) {
            disciplinaDAO.salvar(disciplina, fase.getId(), conn);
        }
    }

    public void salvarManual(Fase fase, int cursoId) {
        String sql = "INSERT INTO tb_fases (nome_fase, curso_id) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNomeFase());
            stmt.setInt(2, cursoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar fase.", e);
        }
    }

    public void atualizar(Fase fase, int cursoId) {
        String sql = "UPDATE tb_fases SET nome_fase = ?, curso_id = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fase.getNomeFase());
            stmt.setInt(2, cursoId);
            stmt.setInt(3, fase.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fase.", e);
        }
    }
    
    public List<Fase> buscarTodos() {
        String sql = "SELECT f.id, f.nome_fase, c.nome_curso FROM tb_fases f JOIN tb_cursos c ON f.curso_id = c.id ORDER BY c.nome_curso, f.nome_fase";
        List<Fase> fases = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fase fase = new Fase();
                fase.setId(rs.getInt("id"));
                fase.setNomeFase(rs.getString("nome_fase"));
                fases.add(fase);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as fases.", e);
        }
        return fases;
    }

    public Fase buscarPorId(int id) {
        String sql = "SELECT * FROM tb_fases WHERE id = ?";
        Fase fase = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    fase = new Fase();
                    fase.setId(rs.getInt("id"));
                    fase.setNomeFase(rs.getString("nome_fase"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fase por ID.", e);
        }
        return fase;
    }

    public List<Fase> buscarPorCurso(int cursoId) {
        String sql = "SELECT * FROM tb_fases WHERE curso_id = ? ORDER BY nome_fase";
        List<Fase> fases = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Fase fase = new Fase();
                    fase.setId(rs.getInt("id"));
                    fase.setNomeFase(rs.getString("nome_fase"));
                    fases.add(fase);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fases por curso.", e);
        }
        return fases;
    }

    public void excluir(int id) {
        String sql = "DELETE FROM tb_fases WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir fase.", e);
        }
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Disciplina;
import model.Professor;

public class DisciplinaDAO {

    public void salvar(Disciplina disciplina, int faseId, Connection conn) throws SQLException {
        String sqlDisciplina = "INSERT INTO tb_disciplinas (codigo_disciplina, nome_disciplina, dia_semana, fase_id) VALUES (?, ?, ?, ?)";
        String sqlVinculo = "INSERT INTO tb_disciplina_professor (disciplina_id, professor_id) VALUES (?, ?)";
        try (PreparedStatement stmtDisciplina = conn.prepareStatement(sqlDisciplina, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmtDisciplina.setString(1, disciplina.getCodigoDisciplina());
            stmtDisciplina.setString(2, disciplina.getNomeDisciplina());
            stmtDisciplina.setInt(3, disciplina.getDiaSemana());
            stmtDisciplina.setInt(4, faseId);
            stmtDisciplina.executeUpdate();
            try (ResultSet rs = stmtDisciplina.getGeneratedKeys()) {
                if (rs.next()) {
                    disciplina.setId(rs.getInt(1));
                }
            }
        }
        try (PreparedStatement stmtVinculo = conn.prepareStatement(sqlVinculo)) {
            for (Professor professor : disciplina.getProfessores()) {
                stmtVinculo.setInt(1, disciplina.getId());
                stmtVinculo.setInt(2, professor.getId());
                stmtVinculo.addBatch();
            }
            stmtVinculo.executeBatch();
        }
    }
    
    public void salvarManual(Disciplina disciplina, int faseId) {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            this.salvar(disciplina, faseId, conn);
            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar disciplina manualmente.", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Disciplina> buscarTodos() {
        String sql = "SELECT * FROM tb_disciplinas ORDER BY nome_disciplina";
        List<Disciplina> disciplinas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Disciplina disciplina = new Disciplina();
                disciplina.setId(rs.getInt("id"));
                disciplina.setCodigoDisciplina(rs.getString("codigo_disciplina"));
                disciplina.setNomeDisciplina(rs.getString("nome_disciplina"));
                disciplina.setDiaSemana(rs.getInt("dia_semana"));
                disciplinas.add(disciplina);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar todas as disciplinas.", e);
        }
        return disciplinas;
    }
    
    public void excluir(int id) {
        String sql = "DELETE FROM tb_disciplinas WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir disciplina.", e);
        }
    }

    public void atualizar(Disciplina disciplina) {
        String sqlUpdateDisciplina = "UPDATE tb_disciplinas SET codigo_disciplina = ?, nome_disciplina = ?, dia_semana = ? WHERE id = ?";
        String sqlDeleteVinculos = "DELETE FROM tb_disciplina_professor WHERE disciplina_id = ?";
        String sqlInsertVinculos = "INSERT INTO tb_disciplina_professor (disciplina_id, professor_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateDisciplina)) {
                stmt.setString(1, disciplina.getCodigoDisciplina());
                stmt.setString(2, disciplina.getNomeDisciplina());
                stmt.setInt(3, disciplina.getDiaSemana());
                stmt.setInt(4, disciplina.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteVinculos)) {
                stmt.setInt(1, disciplina.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertVinculos)) {
                for (Professor professor : disciplina.getProfessores()) {
                    stmt.setInt(1, disciplina.getId());
                    stmt.setInt(2, professor.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar disciplina.", e);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    public Disciplina buscarPorId(int id) {
        String sqlDisciplina = "SELECT * FROM tb_disciplinas WHERE id = ?";
        String sqlProfessores = "SELECT p.* FROM tb_professores p JOIN tb_disciplina_professor dp ON p.id = dp.professor_id WHERE dp.disciplina_id = ?";
        Disciplina disciplina = null;

        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlDisciplina)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        disciplina = new Disciplina();
                        disciplina.setId(rs.getInt("id"));
                        disciplina.setCodigoDisciplina(rs.getString("codigo_disciplina"));
                        disciplina.setNomeDisciplina(rs.getString("nome_disciplina"));
                        disciplina.setDiaSemana(rs.getInt("dia_semana"));
                    }
                }
            }
            if (disciplina != null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlProfessores)) {
                    stmt.setInt(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Professor professor = new Professor();
                            professor.setId(rs.getInt("id"));
                            professor.setNomeProfessor(rs.getString("nome_professor"));
                            professor.setTituloDocente(rs.getInt("titulo_docente"));
                            disciplina.addProfessor(professor);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar disciplina por ID.", e);
        }
        return disciplina;
    }

    public List<Disciplina> buscarPorFase(int faseId) {
        String sql = "SELECT * FROM tb_disciplinas WHERE fase_id = ? ORDER BY nome_disciplina";
        List<Disciplina> disciplinas = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, faseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disciplina disciplina = new Disciplina();
                    disciplina.setId(rs.getInt("id"));
                    disciplina.setCodigoDisciplina(rs.getString("codigo_disciplina"));
                    disciplina.setNomeDisciplina(rs.getString("nome_disciplina"));
                    disciplina.setDiaSemana(rs.getInt("dia_semana"));
                    disciplinas.add(disciplina);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplinas por fase.", e);
        }
        return disciplinas;
    }
}
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
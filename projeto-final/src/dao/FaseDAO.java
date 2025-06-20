package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
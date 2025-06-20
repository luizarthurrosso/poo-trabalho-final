package test;

import java.util.List;

import dao.CursoDAO;
import dao.DisciplinaDAO;
import dao.FaseDAO;
import model.Curso;
import model.Disciplina;
import model.Fase;
import model.Professor;

public class TesteBancoDados {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO TESTE DE LEITURA COMPLETA DO BANCO DE DADOS ---");
        System.out.println("============================================================");

        try {
            CursoDAO cursoDAO = new CursoDAO();
            FaseDAO faseDAO = new FaseDAO();
            DisciplinaDAO disciplinaDAO = new DisciplinaDAO();

            List<Curso> cursos = cursoDAO.buscarTodos();

            if (cursos.isEmpty()) {
                System.out.println("Nenhum curso encontrado no banco de dados.");
            } else {
                for (Curso curso : cursos) {
                    System.out.printf("CURSO: %s (ID: %d)\n", curso.getNomeCurso(), curso.getId());

                    List<Fase> fases = faseDAO.buscarPorCurso(curso.getId());
                    if (fases.isEmpty()) {
                        System.out.println("  -> Nenhuma fase cadastrada para este curso.");
                    } else {
                        for (Fase fase : fases) {
                            System.out.printf("  -> FASE: %s (ID: %d)\n", fase.getNomeFase(), fase.getId());

                            List<Disciplina> disciplinas = disciplinaDAO.buscarPorFase(fase.getId());
                            if (disciplinas.isEmpty()) {
                                System.out.println("    --> Nenhuma disciplina cadastrada para esta fase.");
                            } else {
                                for (Disciplina d : disciplinas) {
                                    Disciplina disciplinaCompleta = disciplinaDAO.buscarPorId(d.getId());
                                    System.out.printf("    --> DISCIPLINA: %s (ID: %d)\n", disciplinaCompleta.getNomeDisciplina(), disciplinaCompleta.getId());

                                    List<Professor> professores = disciplinaCompleta.getProfessores();
                                    if (professores.isEmpty()) {
                                        System.out.println("      ------> Nenhum professor vinculado a esta disciplina.");
                                    } else {
                                        for (Professor professor : professores) {
                                            System.out.printf("      ------> Professor: %s\n", professor.getNomeProfessor());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("============================================================");
                }
            }

        } catch (Exception e) {
            System.err.println("\nOcorreu um erro durante o teste de leitura do banco:");
            e.printStackTrace();
        }
    }
}
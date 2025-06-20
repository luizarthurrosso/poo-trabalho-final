package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.ArquivoProcessadoDAO;
import dao.CursoDAO;
import dao.ProfessorDAO;
import model.Curso;
import model.Disciplina;
import model.Fase;
import model.Professor;
import util.FileHasher;
import util.TabelasAuxiliares;

public class ImportacaoService {

    private final ArquivoProcessadoDAO arquivoProcessadoDAO = new ArquivoProcessadoDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final ProfessorDAO professorDAO = new ProfessorDAO();

    public void importarArquivo(String caminhoDoArquivo) throws IOException, NoSuchAlgorithmException {
        File arquivo = new File(caminhoDoArquivo);
        
        String hash = FileHasher.hash(arquivo);
        if (arquivoProcessadoDAO.hashJaExiste(hash)) {
            throw new RuntimeException("Arquivo já foi processado anteriormente.");
        }

        List<String> linhas = Files.readAllLines(arquivo.toPath());
        
        Curso curso = new Curso();
        Fase faseAtual = null;
        Disciplina disciplinaAtual = null;
        
        for (String linha : linhas) {
            if (linha.isEmpty()) continue;
            
            String tipoRegistro = linha.substring(0, 1);
            
            switch (tipoRegistro) {
                case "0":
                    parseHeader(linha, curso);
                    break;
                case "1":
                    faseAtual = parseFase(linha);
                    curso.addFase(faseAtual);
                    break;
                case "2":
                    if (faseAtual != null) {
                        disciplinaAtual = parseDisciplina(linha);
                        faseAtual.addDisciplina(disciplinaAtual);
                    }
                    break;
                case "3":
                    if (disciplinaAtual != null) {
                        Professor professor = parseProfessor(linha);
                        professorDAO.salvar(professor); 
                        disciplinaAtual.addProfessor(professor);
                    }
                    break;
                case "9":
                    break;
                default:
                    System.out.println("Tipo de registro desconhecido: " + tipoRegistro);
                    break;
            }
        }
        
        cursoDAO.salvar(curso);
        arquivoProcessadoDAO.salvarRegistro(arquivo.getName(), hash);
    }
    
    private void parseHeader(String linha, Curso curso) {
        curso.setNomeCurso(linha.substring(1, 51).trim()); // Início 2, Tam 50
        String dataStr = linha.substring(51, 59).trim(); // Início 52, Tam 8
        curso.setDataProcessamento(LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyyMMdd")));
        curso.setSequenciaArquivo(Integer.parseInt(linha.substring(73, 80).trim())); // Início 74, Tam 7
        curso.setVersaoLayout(linha.substring(80, 83).trim()); // Início 81, Tam 3
    }

    private Fase parseFase(String linha) {
        Fase fase = new Fase();
        fase.setNomeFase(linha.substring(1, 8).trim()); // Início 2, Tam 7
        return fase;
    }

    private Disciplina parseDisciplina(String linha) {
        Disciplina disciplina = new Disciplina();
        String codigo = linha.substring(1, 7).trim(); // Início 2, Tam 6
        disciplina.setCodigoDisciplina(codigo);
        disciplina.setNomeDisciplina(TabelasAuxiliares.getNomeDisciplina(codigo));
        disciplina.setDiaSemana(Integer.parseInt(linha.substring(6, 8).trim())); // Início 7, Tam 2
        return disciplina;
    }

    private Professor parseProfessor(String linha) {
        Professor professor = new Professor();
        professor.setNomeProfessor(linha.substring(1, 41).trim()); // Início 2, Tam 40
        professor.setTituloDocente(Integer.parseInt(linha.substring(41, 43).trim())); // Início 42, Tam 2
        return professor;
    }
}
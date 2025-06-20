package view;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    private TelaImportacao telaImportacao;
    private TelaManutencaoProfessores telaManutencaoProfessores;
    private TelaManutencaoCursos telaManutencaoCursos;
    private TelaManutencaoFases telaManutencaoFases;
    private TelaManutencaoDisciplinas telaManutencaoDisciplinas;

    public TelaPrincipal() {
        super("Sistema Escolar - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        JPanel painel = new JPanel(new GridLayout(5, 1, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton botaoImportar = new JButton("Importar Arquivo de Cursos");
        JButton botaoGerenciarProfessores = new JButton("Gerenciar Professores");
        JButton botaoGerenciarCursos = new JButton("Gerenciar Cursos");
        JButton botaoGerenciarFases = new JButton("Gerenciar Fases");
        JButton botaoGerenciarDisciplinas = new JButton("Gerenciar Disciplinas");

        painel.add(botaoImportar);
        painel.add(botaoGerenciarProfessores);
        painel.add(botaoGerenciarCursos);
        painel.add(botaoGerenciarFases);
        painel.add(botaoGerenciarDisciplinas);
        
        add(painel);

        botaoImportar.addActionListener(e -> {
            if (telaImportacao == null || !telaImportacao.isShowing()) {
                telaImportacao = new TelaImportacao();
                telaImportacao.setVisible(true);
            } else {
                telaImportacao.toFront();
            }
        });

        botaoGerenciarProfessores.addActionListener(e -> {
            if (telaManutencaoProfessores == null || !telaManutencaoProfessores.isShowing()) {
                telaManutencaoProfessores = new TelaManutencaoProfessores();
                telaManutencaoProfessores.setVisible(true);
            } else {
                telaManutencaoProfessores.toFront();
            }
        });

        botaoGerenciarCursos.addActionListener(e -> {
            if (telaManutencaoCursos == null || !telaManutencaoCursos.isShowing()) {
                telaManutencaoCursos = new TelaManutencaoCursos();
                telaManutencaoCursos.setVisible(true);
            } else {
                telaManutencaoCursos.toFront();
            }
        });

        botaoGerenciarFases.addActionListener(e -> {
            if (telaManutencaoFases == null || !telaManutencaoFases.isShowing()) {
                telaManutencaoFases = new TelaManutencaoFases();
                telaManutencaoFases.setVisible(true);
            } else {
                telaManutencaoFases.toFront();
            }
        });

        botaoGerenciarDisciplinas.addActionListener(e -> {
            if (telaManutencaoDisciplinas == null || !telaManutencaoDisciplinas.isShowing()) {
                telaManutencaoDisciplinas = new TelaManutencaoDisciplinas();
                telaManutencaoDisciplinas.setVisible(true);
            } else {
                telaManutencaoDisciplinas.toFront();
            }
        });
    }
}
package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.CursoDAO;
import model.Curso;

public class TelaManutencaoCursos extends JFrame {

    private final CursoDAO cursoDAO;
    private final JTable tabelaCursos;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoNome;
    private final JTextField campoData;
    private final JTextField campoSequencia;
    private final JTextField campoLayout;

    public TelaManutencaoCursos() {
        super("Manutenção de Cursos");
        this.cursoDAO = new CursoDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Data Proc.", "Sequência", "Layout"}, 0);
        tabelaCursos = new JTable(modeloTabela);
        tabelaCursos.removeColumn(tabelaCursos.getColumnModel().getColumn(0));

        carregarDadosTabela();

        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        campoNome = new JTextField();
        campoData = new JTextField();
        campoSequencia = new JTextField();
        campoLayout = new JTextField();
        JButton botaoSalvar = new JButton("Salvar");
        
        painelFormulario.add(new JLabel("Nome do Curso:"));
        painelFormulario.add(campoNome);
        painelFormulario.add(new JLabel("Data Processamento (yyyy-MM-dd):"));
        painelFormulario.add(campoData);
        painelFormulario.add(new JLabel("Sequência:"));
        painelFormulario.add(campoSequencia);
        painelFormulario.add(new JLabel("Versão Layout:"));
        painelFormulario.add(campoLayout);
        painelFormulario.add(new JLabel());
        painelFormulario.add(botaoSalvar);

        JButton botaoExcluir = new JButton("Excluir Selecionado");
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoesAcao.add(botaoExcluir);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.NORTH);
        painelSul.add(painelBotoesAcao, BorderLayout.SOUTH);

        add(new JScrollPane(tabelaCursos), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        botaoSalvar.addActionListener(e -> salvarCurso());
        botaoExcluir.addActionListener(e -> excluirCurso());
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        List<Curso> cursos = cursoDAO.buscarTodos();
        for (Curso c : cursos) {
            modeloTabela.addRow(new Object[]{
                c.getId(),
                c.getNomeCurso(),
                c.getDataProcessamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                c.getSequenciaArquivo(),
                c.getVersaoLayout()
            });
        }
    }

    private void salvarCurso() {
        try {
            Curso novoCurso = new Curso();
            novoCurso.setNomeCurso(campoNome.getText());
            novoCurso.setDataProcessamento(LocalDate.parse(campoData.getText()));
            novoCurso.setSequenciaArquivo(Integer.parseInt(campoSequencia.getText()));
            novoCurso.setVersaoLayout(campoLayout.getText());

            cursoDAO.salvarManual(novoCurso);
            
            JOptionPane.showMessageDialog(this, "Curso salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            campoNome.setText("");
            campoData.setText("");
            campoSequencia.setText("");
            campoLayout.setText("");
            carregarDadosTabela();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-MM-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A sequência deve ser um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCurso() {
        int linhaSelecionada = tabelaCursos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um curso na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idParaExcluir = (int) modeloTabela.getValueAt(tabelaCursos.convertRowIndexToModel(linhaSelecionada), 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir o curso? (Isso excluirá suas fases e disciplinas em cascata)", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            cursoDAO.excluir(idParaExcluir);
            carregarDadosTabela();
        }
    }
}
package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.ListSelectionModel;
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
    private Curso cursoEmEdicao;

    public TelaManutencaoCursos() {
        super("Manutenção de Cursos");
        this.cursoDAO = new CursoDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Data Proc.", "Sequência", "Layout"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaCursos = new JTable(modeloTabela);
        tabelaCursos.removeColumn(tabelaCursos.getColumnModel().getColumn(0));
        tabelaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelFormulario = new JPanel(new GridLayout(5, 2, 5, 5));
        campoNome = new JTextField();
        campoData = new JTextField();
        campoSequencia = new JTextField();
        campoLayout = new JTextField();
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoNovo = new JButton("Novo/Limpar");
        
        painelFormulario.add(new JLabel("Nome do Curso:"));
        painelFormulario.add(campoNome);
        painelFormulario.add(new JLabel("Data Processamento (yyyy-MM-dd):"));
        painelFormulario.add(campoData);
        painelFormulario.add(new JLabel("Sequência:"));
        painelFormulario.add(campoSequencia);
        painelFormulario.add(new JLabel("Versão Layout:"));
        painelFormulario.add(campoLayout);
        painelFormulario.add(botaoNovo);
        painelFormulario.add(botaoSalvar);

        JButton botaoExcluir = new JButton("Excluir Selecionado");
        
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.NORTH);
        painelSul.add(botaoExcluir, BorderLayout.EAST);

        add(new JScrollPane(tabelaCursos), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        carregarDadosTabela();

        botaoSalvar.addActionListener(e -> salvarCurso());
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoExcluir.addActionListener(e -> excluirCurso());
        
        tabelaCursos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    carregarCursoParaEdicao();
                }
            }
        });
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

    private void limparFormulario() {
        this.cursoEmEdicao = null;
        campoNome.setText("");
        campoData.setText("");
        campoSequencia.setText("");
        campoLayout.setText("");
        tabelaCursos.clearSelection();
    }

    private void carregarCursoParaEdicao() {
        int linhaSelecionada = tabelaCursos.getSelectedRow();
        if (linhaSelecionada == -1) return;

        int idCurso = (int) modeloTabela.getValueAt(tabelaCursos.convertRowIndexToModel(linhaSelecionada), 0);
        this.cursoEmEdicao = cursoDAO.buscarPorId(idCurso);

        campoNome.setText(cursoEmEdicao.getNomeCurso());
        campoData.setText(cursoEmEdicao.getDataProcessamento().toString());
        campoSequencia.setText(String.valueOf(cursoEmEdicao.getSequenciaArquivo()));
        campoLayout.setText(cursoEmEdicao.getVersaoLayout());
    }

    private void salvarCurso() {
        try {
            String nome = campoNome.getText();
            LocalDate data = LocalDate.parse(campoData.getText());
            int sequencia = Integer.parseInt(campoSequencia.getText());
            String layout = campoLayout.getText();

            if (nome.trim().isEmpty() || layout.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cursoEmEdicao == null) {
                Curso novoCurso = new Curso();
                novoCurso.setNomeCurso(nome);
                novoCurso.setDataProcessamento(data);
                novoCurso.setSequenciaArquivo(sequencia);
                novoCurso.setVersaoLayout(layout);
                cursoDAO.salvarManual(novoCurso);
                JOptionPane.showMessageDialog(this, "Curso salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                cursoEmEdicao.setNomeCurso(nome);
                cursoEmEdicao.setDataProcessamento(data);
                cursoEmEdicao.setSequenciaArquivo(sequencia);
                cursoEmEdicao.setVersaoLayout(layout);
                cursoDAO.atualizar(cursoEmEdicao);
                JOptionPane.showMessageDialog(this, "Curso atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            limparFormulario();
            carregarDadosTabela();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use yyyy-MM-dd.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A sequência deve ser um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCurso() {
        if (cursoEmEdicao == null) {
            JOptionPane.showMessageDialog(this, "Selecione um curso na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir o curso? (Isso excluirá suas fases e disciplinas em cascata)", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            cursoDAO.excluir(cursoEmEdicao.getId());
            limparFormulario();
            carregarDadosTabela();
        }
    }
}
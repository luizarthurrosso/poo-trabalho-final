package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.ConnectionFactory;
import dao.CursoDAO;
import dao.FaseDAO;
import model.Curso;
import model.Fase;

public class TelaManutencaoFases extends JFrame {

    private final FaseDAO faseDAO;
    private final CursoDAO cursoDAO;
    private final JTable tabelaFases;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoNomeFase;
    private final JComboBox<CursoItem> comboBoxCursos;
    private Fase faseEmEdicao;

    public TelaManutencaoFases() {
        super("Manutenção de Fases");
        this.faseDAO = new FaseDAO();
        this.cursoDAO = new CursoDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Curso ID", "Nome da Fase", "Curso Pertencente"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaFases = new JTable(modeloTabela);
        tabelaFases.removeColumn(tabelaFases.getColumnModel().getColumn(0)); // Esconde ID da Fase
        tabelaFases.removeColumn(tabelaFases.getColumnModel().getColumn(0)); // Esconde ID do Curso
        tabelaFases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        campoNomeFase = new JTextField();
        comboBoxCursos = new JComboBox<>();
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoNovo = new JButton("Novo/Limpar");

        painelFormulario.add(new JLabel("Nome da Fase:"));
        painelFormulario.add(campoNomeFase);
        painelFormulario.add(new JLabel("Curso:"));
        painelFormulario.add(comboBoxCursos);
        painelFormulario.add(botaoNovo);
        painelFormulario.add(botaoSalvar);

        JButton botaoExcluir = new JButton("Excluir Selecionada");
        
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.NORTH);
        painelSul.add(botaoExcluir, BorderLayout.EAST);
        
        add(new JScrollPane(tabelaFases), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        carregarCursosComboBox();
        carregarDadosTabela();

        botaoSalvar.addActionListener(e -> salvarFase());
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoExcluir.addActionListener(e -> excluirFase());

        tabelaFases.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    carregarFaseParaEdicao();
                }
            }
        });
    }
    
    private void carregarCursosComboBox() {
        List<Curso> cursos = cursoDAO.buscarTodos();
        for (Curso c : cursos) {
            comboBoxCursos.addItem(new CursoItem(c.getId(), c.getNomeCurso()));
        }
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        String sql = "SELECT f.id, f.nome_fase, c.id AS curso_id, c.nome_curso FROM tb_fases f JOIN tb_cursos c ON f.curso_id = c.id ORDER BY c.nome_curso, f.nome_fase";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("curso_id"),
                    rs.getString("nome_fase"),
                    rs.getString("nome_curso")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void limparFormulario() {
        this.faseEmEdicao = null;
        campoNomeFase.setText("");
        if (comboBoxCursos.getItemCount() > 0) {
            comboBoxCursos.setSelectedIndex(0);
        }
        tabelaFases.clearSelection();
    }

    private void carregarFaseParaEdicao() {
        int linhaSelecionada = tabelaFases.getSelectedRow();
        if (linhaSelecionada == -1) return;

        int idFase = (int) modeloTabela.getValueAt(tabelaFases.convertRowIndexToModel(linhaSelecionada), 0);
        int idCurso = (int) modeloTabela.getValueAt(tabelaFases.convertRowIndexToModel(linhaSelecionada), 1);
        String nomeFase = (String) modeloTabela.getValueAt(tabelaFases.convertRowIndexToModel(linhaSelecionada), 2);
        
        this.faseEmEdicao = new Fase();
        faseEmEdicao.setId(idFase);
        faseEmEdicao.setNomeFase(nomeFase);
        
        campoNomeFase.setText(nomeFase);

        for (int i = 0; i < comboBoxCursos.getItemCount(); i++) {
            CursoItem item = comboBoxCursos.getItemAt(i);
            if (item.getId() == idCurso) {
                comboBoxCursos.setSelectedIndex(i);
                break;
            }
        }
    }

    private void salvarFase() {
        String nome = campoNomeFase.getText();
        if (nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da fase é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        CursoItem cursoSelecionado = (CursoItem) comboBoxCursos.getSelectedItem();
        if (cursoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um curso.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (faseEmEdicao == null) {
            Fase novaFase = new Fase();
            novaFase.setNomeFase(nome);
            faseDAO.salvarManual(novaFase, cursoSelecionado.getId());
            JOptionPane.showMessageDialog(this, "Fase salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
             faseEmEdicao.setNomeFase(nome);
             faseDAO.atualizar(faseEmEdicao, cursoSelecionado.getId());
             JOptionPane.showMessageDialog(this, "Fase atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
        
        limparFormulario();
        carregarDadosTabela();
    }
    
    private void excluirFase() {
        int linhaSelecionada = tabelaFases.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma fase para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idParaExcluir = (int) modeloTabela.getValueAt(tabelaFases.convertRowIndexToModel(linhaSelecionada), 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir a fase? (Suas disciplinas serão excluídas em cascata)", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            faseDAO.excluir(idParaExcluir);
            limparFormulario();
            carregarDadosTabela();
        }
    }
}
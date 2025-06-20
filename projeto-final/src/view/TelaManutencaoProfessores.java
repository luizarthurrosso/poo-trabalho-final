package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.table.DefaultTableModel;

import dao.ProfessorDAO;
import model.Professor;
import util.TabelasAuxiliares;

public class TelaManutencaoProfessores extends JFrame {

    private final ProfessorDAO professorDAO;
    private final JTable tabelaProfessores;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoNome;
    private final JComboBox<TituloDocenteItem> comboBoxTitulo;

    public TelaManutencaoProfessores() {
        super("Manutenção de Professores");
        this.professorDAO = new ProfessorDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Título Docente"}, 0);
        tabelaProfessores = new JTable(modeloTabela);
        tabelaProfessores.removeColumn(tabelaProfessores.getColumnModel().getColumn(0));

        carregarDadosTabela();

        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        campoNome = new JTextField();
        
        comboBoxTitulo = new JComboBox<>();
        comboBoxTitulo.addItem(new TituloDocenteItem(1, "Pós-Graduação"));
        comboBoxTitulo.addItem(new TituloDocenteItem(2, "Mestrado"));
        comboBoxTitulo.addItem(new TituloDocenteItem(3, "Doutorado"));
        comboBoxTitulo.addItem(new TituloDocenteItem(4, "Pós-Doutorado"));

        JButton botaoSalvar = new JButton("Salvar");
        
        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(campoNome);
        painelFormulario.add(new JLabel("Título:"));
        painelFormulario.add(comboBoxTitulo);
        painelFormulario.add(new JLabel());
        painelFormulario.add(botaoSalvar);

        JButton botaoExcluir = new JButton("Excluir Selecionado");
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoesAcao.add(botaoExcluir);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.NORTH);
        painelSul.add(painelBotoesAcao, BorderLayout.SOUTH);

        add(new JScrollPane(tabelaProfessores), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        botaoSalvar.addActionListener(e -> salvarProfessor());
        botaoExcluir.addActionListener(e -> excluirProfessor());
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        List<Professor> professores = professorDAO.buscarTodos();
        for (Professor p : professores) {
            String tituloDescricao = TabelasAuxiliares.getTituloDocente(p.getTituloDocente());
            modeloTabela.addRow(new Object[]{p.getId(), p.getNomeProfessor(), tituloDescricao});
        }
    }

    private void salvarProfessor() {
        String nome = campoNome.getText();
        if (nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TituloDocenteItem itemSelecionado = (TituloDocenteItem) comboBoxTitulo.getSelectedItem();
        if (itemSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um título.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Professor novoProfessor = new Professor();
        novoProfessor.setNomeProfessor(nome);
        novoProfessor.setTituloDocente(itemSelecionado.getId());

        professorDAO.salvar(novoProfessor);
        
        JOptionPane.showMessageDialog(this, "Professor salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        campoNome.setText("");
        comboBoxTitulo.setSelectedIndex(0);
        carregarDadosTabela();
    }

    private void excluirProfessor() {
        int linhaSelecionada = tabelaProfessores.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um professor na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idParaExcluir = (int) modeloTabela.getValueAt(tabelaProfessores.convertRowIndexToModel(linhaSelecionada), 0);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o professor selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            professorDAO.excluir(idParaExcluir);
            carregarDadosTabela();
        }
    }
}
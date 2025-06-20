package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import dao.ProfessorDAO;
import model.Professor;
import util.TabelasAuxiliares;

public class TelaManutencaoProfessores extends JFrame {

    private final ProfessorDAO professorDAO;
    private final JTable tabelaProfessores;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoNome;
    private final JComboBox<TituloDocenteItem> comboBoxTitulo;
    private Professor professorEmEdicao;

    public TelaManutencaoProfessores() {
        super("Manutenção de Professores");
        this.professorDAO = new ProfessorDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Título Docente"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProfessores = new JTable(modeloTabela);
        tabelaProfessores.removeColumn(tabelaProfessores.getColumnModel().getColumn(0));
        tabelaProfessores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel painelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        campoNome = new JTextField();
        comboBoxTitulo = new JComboBox<>();
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoNovo = new JButton("Novo/Limpar");

        painelFormulario.add(new JLabel("Nome:"));
        painelFormulario.add(campoNome);
        painelFormulario.add(new JLabel("Título:"));
        painelFormulario.add(comboBoxTitulo);
        painelFormulario.add(botaoNovo);
        painelFormulario.add(botaoSalvar);

        JButton botaoExcluir = new JButton("Excluir Selecionado");
        
        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelFormulario, BorderLayout.CENTER);
        painelSul.add(botaoExcluir, BorderLayout.EAST);

        add(new JScrollPane(tabelaProfessores), BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        carregarTitulosComboBox();
        carregarDadosTabela();

        botaoSalvar.addActionListener(e -> salvarProfessor());
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoExcluir.addActionListener(e -> excluirProfessor());
        
        tabelaProfessores.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    carregarProfessorParaEdicao();
                }
            }
        });
    }

    private void carregarTitulosComboBox() {
        comboBoxTitulo.addItem(new TituloDocenteItem(1, "Pós-Graduação"));
        comboBoxTitulo.addItem(new TituloDocenteItem(2, "Mestrado"));
        comboBoxTitulo.addItem(new TituloDocenteItem(3, "Doutorado"));
        comboBoxTitulo.addItem(new TituloDocenteItem(4, "Pós-Doutorado"));
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        List<Professor> professores = professorDAO.buscarTodos();
        for (Professor p : professores) {
            String tituloDescricao = TabelasAuxiliares.getTituloDocente(p.getTituloDocente());
            modeloTabela.addRow(new Object[]{p.getId(), p.getNomeProfessor(), tituloDescricao});
        }
    }

    private void limparFormulario() {
        this.professorEmEdicao = null;
        campoNome.setText("");
        comboBoxTitulo.setSelectedIndex(0);
        tabelaProfessores.clearSelection();
    }

    private void carregarProfessorParaEdicao() {
        int linhaSelecionada = tabelaProfessores.getSelectedRow();
        if (linhaSelecionada == -1) return;

        int idProfessor = (int) modeloTabela.getValueAt(tabelaProfessores.convertRowIndexToModel(linhaSelecionada), 0);
        this.professorEmEdicao = professorDAO.buscarPorId(idProfessor);

        campoNome.setText(professorEmEdicao.getNomeProfessor());
        for (int i = 0; i < comboBoxTitulo.getItemCount(); i++) {
            if (comboBoxTitulo.getItemAt(i).getId() == professorEmEdicao.getTituloDocente()) {
                comboBoxTitulo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void salvarProfessor() {
        String nome = campoNome.getText();
        if (nome.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome é obrigatório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TituloDocenteItem itemSelecionado = (TituloDocenteItem) comboBoxTitulo.getSelectedItem();
        if (itemSelecionado == null) return;

        if (professorEmEdicao == null) {
            Professor novoProfessor = new Professor();
            novoProfessor.setNomeProfessor(nome);
            novoProfessor.setTituloDocente(itemSelecionado.getId());
            professorDAO.salvar(novoProfessor);
            JOptionPane.showMessageDialog(this, "Professor salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            professorEmEdicao.setNomeProfessor(nome);
            professorEmEdicao.setTituloDocente(itemSelecionado.getId());
            professorDAO.atualizar(professorEmEdicao);
            JOptionPane.showMessageDialog(this, "Professor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
        
        limparFormulario();
        carregarDadosTabela();
    }

    private void excluirProfessor() {
        if (professorEmEdicao == null) {
            JOptionPane.showMessageDialog(this, "Selecione um professor na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o professor selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            professorDAO.excluir(professorEmEdicao.getId());
            limparFormulario();
            carregarDadosTabela();
        }
    }
}
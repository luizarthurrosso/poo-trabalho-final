package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.DisciplinaDAO;
import dao.FaseDAO;
import dao.ProfessorDAO;
import model.Disciplina;
import model.Fase;
import model.Professor;

public class TelaManutencaoDisciplinas extends JFrame {

    private final DisciplinaDAO disciplinaDAO;
    private final FaseDAO faseDAO;
    private final ProfessorDAO professorDAO;

    private final JTable tabelaDisciplinas;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoCodigo;
    private final JTextField campoNome;
    private final JComboBox<DiaSemanaItem> comboBoxDiaSemana;
    private final JComboBox<FaseItem> comboBoxFases;
    private final JList<Professor> listaProfessores;
    
    private Disciplina disciplinaEmEdicao;

    public TelaManutencaoDisciplinas() {
        super("Manutenção de Disciplinas");
        
        this.disciplinaDAO = new DisciplinaDAO();
        this.faseDAO = new FaseDAO();
        this.professorDAO = new ProfessorDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Código", "Nome", "Dia Semana (Cod)"}, 0);
        tabelaDisciplinas = new JTable(modeloTabela);
        tabelaDisciplinas.removeColumn(tabelaDisciplinas.getColumnModel().getColumn(0));
        tabelaDisciplinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Dados da Disciplina"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoCodigo = new JTextField(10);
        campoNome = new JTextField(30);
        comboBoxDiaSemana = new JComboBox<>();
        comboBoxFases = new JComboBox<>();
        listaProfessores = new JList<>();
        listaProfessores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollProfessores = new JScrollPane(listaProfessores);
        scrollProfessores.setPreferredSize(new Dimension(250, 120));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; painelFormulario.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; painelFormulario.add(campoCodigo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; painelFormulario.add(campoNome, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Dia da Semana:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; painelFormulario.add(comboBoxDiaSemana, gbc);
        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Fase:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; painelFormulario.add(comboBoxFases, gbc);

        gbc.gridx = 2; gbc.gridy = 0; painelFormulario.add(new JLabel("Professores:"), gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 4; gbc.fill = GridBagConstraints.BOTH; painelFormulario.add(scrollProfessores, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridheight = 1;

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoNovo = new JButton("Novo/Limpar");
        painelBotoesForm.add(botaoSalvar);
        painelBotoesForm.add(botaoNovo);
        gbc.gridx = 1; gbc.gridy = 4; painelFormulario.add(painelBotoesForm, gbc);

        JButton botaoExcluir = new JButton("Excluir Selecionada");
        
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);
        painelTabela.add(botaoExcluir, BorderLayout.SOUTH);

        add(painelTabela, BorderLayout.CENTER);
        add(painelFormulario, BorderLayout.SOUTH);

        carregarDiasSemanaComboBox();
        carregarFasesComboBox();
        carregarProfessoresList();
        carregarDadosTabela();

        botaoSalvar.addActionListener(e -> salvarDisciplina());
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoExcluir.addActionListener(e -> excluirDisciplina());
        
        tabelaDisciplinas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    carregarDisciplinaParaEdicao();
                }
            }
        });
    }

    private void carregarDiasSemanaComboBox() {
        comboBoxDiaSemana.addItem(new DiaSemanaItem(2, "Segunda-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(3, "Terça-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(4, "Quarta-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(5, "Quinta-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(6, "Sexta-Feira"));
    }

    private void carregarFasesComboBox() {
        List<Fase> fases = faseDAO.buscarTodos();
        for (Fase f : fases) {
            comboBoxFases.addItem(new FaseItem(f.getId(), f.getNomeFase()));
        }
    }

    private void carregarProfessoresList() {
        DefaultListModel<Professor> listModel = new DefaultListModel<>();
        List<Professor> professores = professorDAO.buscarTodos();
        for (Professor p : professores) {
            listModel.addElement(p);
        }
        listaProfessores.setModel(listModel);
    }

    private void carregarDadosTabela() {
        modeloTabela.setRowCount(0);
        List<Disciplina> disciplinas = disciplinaDAO.buscarTodos();
        for (Disciplina d : disciplinas) {
            modeloTabela.addRow(new Object[]{ d.getId(), d.getCodigoDisciplina(), d.getNomeDisciplina(), d.getDiaSemana() });
        }
    }

    private void limparFormulario() {
        this.disciplinaEmEdicao = null;
        campoCodigo.setText("");
        campoNome.setText("");
        comboBoxDiaSemana.setSelectedIndex(0);
        comboBoxFases.setSelectedIndex(0);
        listaProfessores.clearSelection();
        tabelaDisciplinas.clearSelection();
    }

    private void carregarDisciplinaParaEdicao() {
        int linhaSelecionada = tabelaDisciplinas.getSelectedRow();
        if (linhaSelecionada == -1) return;

        int idDisciplina = (int) modeloTabela.getValueAt(tabelaDisciplinas.convertRowIndexToModel(linhaSelecionada), 0);
        this.disciplinaEmEdicao = disciplinaDAO.buscarPorId(idDisciplina);

        campoCodigo.setText(disciplinaEmEdicao.getCodigoDisciplina());
        campoNome.setText(disciplinaEmEdicao.getNomeDisciplina());

        for (int i = 0; i < comboBoxDiaSemana.getItemCount(); i++) {
            if (comboBoxDiaSemana.getItemAt(i).getId() == disciplinaEmEdicao.getDiaSemana()) {
                comboBoxDiaSemana.setSelectedIndex(i);
                break;
            }
        }
        
        listaProfessores.clearSelection();
        List<Integer> idsProfessoresAssociados = new ArrayList<>();
        for (Professor p : disciplinaEmEdicao.getProfessores()) {
            idsProfessoresAssociados.add(p.getId());
        }
        
        List<Integer> indicesParaSelecionar = new ArrayList<>();
        DefaultListModel<Professor> model = (DefaultListModel<Professor>) listaProfessores.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (idsProfessoresAssociados.contains(model.getElementAt(i).getId())) {
                indicesParaSelecionar.add(i);
            }
        }
        listaProfessores.setSelectedIndices(indicesParaSelecionar.stream().mapToInt(i -> i).toArray());
    }

    private void salvarDisciplina() {
        try {
            String codigo = campoCodigo.getText();
            String nome = campoNome.getText();
            DiaSemanaItem diaSelecionado = (DiaSemanaItem) comboBoxDiaSemana.getSelectedItem();
            FaseItem faseSelecionada = (FaseItem) comboBoxFases.getSelectedItem();
            List<Professor> professoresSelecionados = listaProfessores.getSelectedValuesList();

            if (nome.trim().isEmpty() || codigo.trim().isEmpty() || diaSelecionado == null || faseSelecionada == null || professoresSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (disciplinaEmEdicao == null) {
                Disciplina novaDisciplina = new Disciplina();
                novaDisciplina.setCodigoDisciplina(codigo);
                novaDisciplina.setNomeDisciplina(nome);
                novaDisciplina.setDiaSemana(diaSelecionado.getId());
                novaDisciplina.setProfessores(professoresSelecionados);
                disciplinaDAO.salvarManual(novaDisciplina, faseSelecionada.getId());
                JOptionPane.showMessageDialog(this, "Disciplina salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                disciplinaEmEdicao.setCodigoDisciplina(codigo);
                disciplinaEmEdicao.setNomeDisciplina(nome);
                disciplinaEmEdicao.setDiaSemana(diaSelecionado.getId());
                disciplinaEmEdicao.setProfessores(professoresSelecionados);
                disciplinaDAO.atualizar(disciplinaEmEdicao);
                JOptionPane.showMessageDialog(this, "Disciplina atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            
            limparFormulario();
            carregarDadosTabela();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar disciplina: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void excluirDisciplina() {
        if (disciplinaEmEdicao == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma disciplina na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir a disciplina '" + disciplinaEmEdicao.getNomeDisciplina() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            disciplinaDAO.excluir(disciplinaEmEdicao.getId());
            limparFormulario();
            carregarDadosTabela();
        }
    }
}
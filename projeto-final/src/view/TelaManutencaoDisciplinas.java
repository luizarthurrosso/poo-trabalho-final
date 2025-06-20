package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import dao.ConnectionFactory;
import dao.CursoDAO;
import dao.DisciplinaDAO;
import dao.FaseDAO;
import dao.ProfessorDAO;
import model.Curso;
import model.Disciplina;
import model.Fase;
import model.Professor;

public class TelaManutencaoDisciplinas extends JFrame {

    private final DisciplinaDAO disciplinaDAO;
    private final FaseDAO faseDAO;
    private final ProfessorDAO professorDAO;
    private final CursoDAO cursoDAO;

    private final JTable tabelaDisciplinas;
    private final DefaultTableModel modeloTabela;
    private final JTextField campoCodigo;
    private final JTextField campoNome;
    private final JComboBox<DiaSemanaItem> comboBoxDiaSemana;
    private final JComboBox<CursoItem> comboBoxCursos;
    private final JComboBox<FaseItem> comboBoxFases;
    private final JList<Professor> listaProfessores;

    private Disciplina disciplinaEmEdicao;

    public TelaManutencaoDisciplinas() {
        super("Manutenção de Disciplinas");

        this.disciplinaDAO = new DisciplinaDAO();
        this.faseDAO = new FaseDAO();
        this.professorDAO = new ProfessorDAO();
        this.cursoDAO = new CursoDAO();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Código", "Nome", "Dia", "Fase", "Curso"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
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
        comboBoxCursos = new JComboBox<>();
        comboBoxFases = new JComboBox<>();
        listaProfessores = new JList<>();
        listaProfessores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollProfessores = new JScrollPane(listaProfessores);
        scrollProfessores.setPreferredSize(new Dimension(250, 150));

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; painelFormulario.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; painelFormulario.add(comboBoxCursos, gbc);
        gbc.gridx = 0; gbc.gridy = 1; painelFormulario.add(new JLabel("Fase:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; painelFormulario.add(comboBoxFases, gbc);
        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Código Disciplina:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; painelFormulario.add(campoCodigo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; painelFormulario.add(new JLabel("Nome Disciplina:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; painelFormulario.add(campoNome, gbc);
        gbc.gridx = 0; gbc.gridy = 4; painelFormulario.add(new JLabel("Dia da Semana:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; painelFormulario.add(comboBoxDiaSemana, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.anchor = GridBagConstraints.NORTHWEST; painelFormulario.add(new JLabel("Professores:"), gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 5; gbc.fill = GridBagConstraints.BOTH; painelFormulario.add(scrollProfessores, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridheight = 1;

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoNovo = new JButton("Novo/Limpar");
        painelBotoesForm.add(botaoSalvar);
        painelBotoesForm.add(botaoNovo);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.WEST; painelFormulario.add(painelBotoesForm, gbc);

        JButton botaoExcluir = new JButton("Excluir Selecionada");

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);
        painelTabela.add(botaoExcluir, BorderLayout.SOUTH);

        add(painelTabela, BorderLayout.CENTER);
        add(painelFormulario, BorderLayout.SOUTH);

        carregarDiasSemanaComboBox();
        carregarProfessoresList();
        carregarCursosComboBox();
        carregarDadosTabela();

        comboBoxCursos.addActionListener(e -> {
            CursoItem item = (CursoItem) comboBoxCursos.getSelectedItem();
            if (item != null) {
                carregarFasesComboBox(item.getId());
            }
        });

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

        if (comboBoxCursos.getItemCount() > 0) {
            CursoItem primeiroCurso = (CursoItem) comboBoxCursos.getSelectedItem();
            if (primeiroCurso != null) {
                carregarFasesComboBox(primeiroCurso.getId());
            }
        }
    }

    private void carregarDiasSemanaComboBox() {
        comboBoxDiaSemana.addItem(new DiaSemanaItem(2, "Segunda-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(3, "Terça-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(4, "Quarta-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(5, "Quinta-Feira"));
        comboBoxDiaSemana.addItem(new DiaSemanaItem(6, "Sexta-Feira"));
    }

    private void carregarCursosComboBox() {
        comboBoxCursos.removeAllItems();
        List<Curso> cursos = cursoDAO.buscarTodos();
        for (Curso c : cursos) {
            comboBoxCursos.addItem(new CursoItem(c.getId(), c.getNomeCurso()));
        }
    }

    private void carregarFasesComboBox(int cursoId) {
        comboBoxFases.removeAllItems();
        List<Fase> fases = faseDAO.buscarPorCurso(cursoId);
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
        String sql = "SELECT d.id, d.codigo_disciplina, d.nome_disciplina, d.dia_semana, f.nome_fase, c.nome_curso " +
                     "FROM tb_disciplinas d " +
                     "JOIN tb_fases f ON d.fase_id = f.id " +
                     "JOIN tb_cursos c ON f.curso_id = c.id " +
                     "ORDER BY c.nome_curso, f.nome_fase, d.nome_disciplina";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("codigo_disciplina"),
                    rs.getString("nome_disciplina"),
                    rs.getInt("dia_semana"),
                    rs.getString("nome_fase"),
                    rs.getString("nome_curso")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limparFormulario() {
        this.disciplinaEmEdicao = null;
        campoCodigo.setText("");
        campoNome.setText("");
        if (comboBoxCursos.getItemCount() > 0) comboBoxCursos.setSelectedIndex(0);
        if (comboBoxDiaSemana.getItemCount() > 0) comboBoxDiaSemana.setSelectedIndex(0);
        listaProfessores.clearSelection();
        tabelaDisciplinas.clearSelection();
    }

    private void carregarDisciplinaParaEdicao() {
        int linhaSelecionada = tabelaDisciplinas.getSelectedRow();
        if (linhaSelecionada == -1) return;

        int idDisciplina = (int) modeloTabela.getValueAt(tabelaDisciplinas.convertRowIndexToModel(linhaSelecionada), 0);
        this.disciplinaEmEdicao = disciplinaDAO.buscarPorId(idDisciplina);

        if (disciplinaEmEdicao == null) {
            limparFormulario();
            return;
        }

        campoCodigo.setText(disciplinaEmEdicao.getCodigoDisciplina());
        campoNome.setText(disciplinaEmEdicao.getNomeDisciplina());

        for (int i = 0; i < comboBoxDiaSemana.getItemCount(); i++) {
            if (comboBoxDiaSemana.getItemAt(i).getId() == disciplinaEmEdicao.getDiaSemana()) {
                comboBoxDiaSemana.setSelectedIndex(i);
                break;
            }
        }
        
        listaProfessores.clearSelection();
        List<Integer> idsProfessores = new ArrayList<>();
        for (Professor p : disciplinaEmEdicao.getProfessores()) {
            idsProfessores.add(p.getId());
        }
        List<Integer> indicesParaSelecionar = new ArrayList<>();
        DefaultListModel<Professor> model = (DefaultListModel<Professor>) listaProfessores.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (idsProfessores.contains(model.getElementAt(i).getId())) {
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

            Disciplina disciplina = (disciplinaEmEdicao == null) ? new Disciplina() : disciplinaEmEdicao;
            disciplina.setCodigoDisciplina(codigo);
            disciplina.setNomeDisciplina(nome);
            disciplina.setDiaSemana(diaSelecionado.getId());
            disciplina.setProfessores(professoresSelecionados);

            if (disciplinaEmEdicao == null) {
                disciplinaDAO.salvarManual(disciplina, faseSelecionada.getId());
                JOptionPane.showMessageDialog(this, "Disciplina salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                disciplinaDAO.atualizar(disciplina);
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
        if (tabelaDisciplinas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma disciplina na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idParaExcluir = (int) modeloTabela.getValueAt(tabelaDisciplinas.convertRowIndexToModel(tabelaDisciplinas.getSelectedRow()), 0);
        String nomeDisciplina = (String) modeloTabela.getValueAt(tabelaDisciplinas.convertRowIndexToModel(tabelaDisciplinas.getSelectedRow()), 2);
        
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja excluir a disciplina '" + nomeDisciplina + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            disciplinaDAO.excluir(idParaExcluir);
            limparFormulario();
            carregarDadosTabela();
        }
    }
}
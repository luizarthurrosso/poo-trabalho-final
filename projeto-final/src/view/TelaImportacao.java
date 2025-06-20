package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import service.ImportacaoService;

public class TelaImportacao extends JFrame {

    private final JTextArea areaTextoArquivo;
    private final JButton botaoSelecionarArquivo;
    private final JButton botaoImportar;
    private final JLabel labelArquivoSelecionado;
    private File arquivoSelecionado;
    private final ImportacaoService importacaoService;

    public TelaImportacao() {
        super("Importador de Dados Escolares");

        this.importacaoService = new ImportacaoService();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        areaTextoArquivo = new JTextArea();
        areaTextoArquivo.setEditable(false);
        areaTextoArquivo.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTextoArquivo);

        botaoSelecionarArquivo = new JButton("Selecionar Arquivo...");
        botaoImportar = new JButton("Importar para o Banco");
        botaoImportar.setEnabled(false);

        labelArquivoSelecionado = new JLabel("Nenhum arquivo selecionado.");
        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBotoes.add(botaoSelecionarArquivo);
        painelBotoes.add(botaoImportar);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBotoes, BorderLayout.WEST);
        painelSul.add(labelArquivoSelecionado, BorderLayout.CENTER);

        add(scrollPane, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        configurarAcoes();
    }

    private void configurarAcoes() {
        botaoSelecionarArquivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int resultado = fileChooser.showOpenDialog(TelaImportacao.this);

                if (resultado == JFileChooser.APPROVE_OPTION) {
                    arquivoSelecionado = fileChooser.getSelectedFile();
                    labelArquivoSelecionado.setText("Arquivo: " + arquivoSelecionado.getName());
                    try {
                        String conteudo = Files.readString(arquivoSelecionado.toPath());
                        areaTextoArquivo.setText(conteudo);
                        botaoImportar.setEnabled(true);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(TelaImportacao.this, "Erro ao ler o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        botaoImportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (arquivoSelecionado != null) {
                    try {
                        importacaoService.importarArquivo(arquivoSelecionado.getAbsolutePath());
                        JOptionPane.showMessageDialog(TelaImportacao.this, "Arquivo importado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException | NoSuchAlgorithmException | RuntimeException ex) {
                        JOptionPane.showMessageDialog(TelaImportacao.this, "Erro na importação: " + ex.getMessage(), "Erro de Importação", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
}
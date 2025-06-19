package model;

import java.time.LocalDateTime;

public class ArquivoImportado {
    private int id;
    private String hashArquivo;
    private LocalDateTime dataImportacao;

    public ArquivoImportado() {
    }

    public ArquivoImportado(int id, String hashArquivo, LocalDateTime dataImportacao) {
        this.id = id;
        this.hashArquivo = hashArquivo;
        this.dataImportacao = dataImportacao;
    }

    public ArquivoImportado(String hashArquivo) {
        this.hashArquivo = hashArquivo;
        this.dataImportacao = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHashArquivo() {
        return hashArquivo;
    }

    public void setHashArquivo(String hashArquivo) {
        this.hashArquivo = hashArquivo;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }
}

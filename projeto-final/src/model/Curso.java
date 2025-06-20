package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Curso {

    private int id;
    private String nomeCurso;
    private LocalDate dataProcessamento;
    private int sequenciaArquivo;
    private String versaoLayout;
    private List<Fase> fases;

    public Curso() {
        this.fases = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public LocalDate getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDate dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }



    public int getSequenciaArquivo() {
        return sequenciaArquivo;
    }

    public void setSequenciaArquivo(int sequenciaArquivo) {
        this.sequenciaArquivo = sequenciaArquivo;
    }

    public String getVersaoLayout() {
        return versaoLayout;
    }

    public void setVersaoLayout(String versaoLayout) {
        this.versaoLayout = versaoLayout;
    }

    public List<Fase> getFases() {
        return fases;
    }

    public void setFases(List<Fase> fases) {
        this.fases = fases;
    }
    
    public void addFase(Fase fase) {
        this.fases.add(fase);
    }
}
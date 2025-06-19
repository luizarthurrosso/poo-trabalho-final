package model;

public class Disciplina {
    private int id;
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String diaSemanaCodigo;
    private int qtdProfessores;
    private int faseId;

    public Disciplina(){
    }

    public Disciplina(int id, String codigoDisciplina, String nomeDisciplina, String diaSemanaCodigo, int qtdProfessores, int faseId) {
        this.id = id;
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.diaSemanaCodigo = diaSemanaCodigo;
        this.qtdProfessores = qtdProfessores;
        this.faseId = faseId;
    }

    public Disciplina(String codigoDisciplina, String nomeDisciplina, String diaSemanaCodigo, int qtdProfessores, int faseId) {
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.diaSemanaCodigo = diaSemanaCodigo;
        this.qtdProfessores = qtdProfessores;
        this.faseId = faseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public String getDiaSemanaCodigo() {
        return diaSemanaCodigo;
    }

    public void setDiaSemanaCodigo(String diaSemanaCodigo) {
        this.diaSemanaCodigo = diaSemanaCodigo;
    }

    public int getQtdProfessores() {
        return qtdProfessores;
    }

    public void setQtdProfessores(int qtdProfessores) {
        this.qtdProfessores = qtdProfessores;
    }

    public int getFaseId() {
        return faseId;
    }

    public void setFaseId(int faseId) {
        this.faseId = faseId;
    }
}
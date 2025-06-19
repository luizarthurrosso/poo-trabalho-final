package model;

public class Fase {
    private int id;
    private String codigoFase;
    private int qtdDisciplinas;
    private int qtdProfessores;
    private int cursoId;

    public Fase(){
    }

    public Fase(int id, String codigoFase, int qtdDisciplinas, int qtdProfessores, int cursoId ){
        this.id = id;
        this.codigoFase = codigoFase;
        this.qtdDisciplinas = qtdDisciplinas;
        this.qtdProfessores = qtdProfessores;
        this.cursoId = cursoId;
    }

    public Fase(String codigoFase, int qtdDisciplinas, int qtdProfessores, int cursoId ){
        this.codigoFase = codigoFase;
        this.qtdDisciplinas = qtdDisciplinas;
        this.qtdProfessores = qtdProfessores;
        this.cursoId = cursoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoFase() {
        return codigoFase;
    }

    public void setCodigoFase(String codigoFase) {
        this.codigoFase = codigoFase;
    }

    public int getQtdDisciplinas() {
        return qtdDisciplinas;
    }

    public void setQtdDisciplinas(int qtdDisciplinas) {
        this.qtdDisciplinas = qtdDisciplinas;
    }

    public int getQtdProfessores() {
        return qtdProfessores;
    }

    public void setQtdProfessores(int qtdProfessores) {
        this.qtdProfessores = qtdProfessores;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }
}

package model;

public class Professor {

    private int id;
    private String nomeProfessor;
    private int tituloDocente;

    public Professor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public int getTituloDocente() {
        return tituloDocente;
    }

    public void setTituloDocente(int tituloDocente) {
        this.tituloDocente = tituloDocente;
    }

    @Override
    public String toString() {
        return this.nomeProfessor;
    }
}
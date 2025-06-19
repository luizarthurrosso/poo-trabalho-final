package model;

public class Professor {
    private int id;
    private String nome;
    private String tituloDocenteCodigo;
    private String tituloDocenteDescricao;

    public Professor() {
    }

    public Professor(int id, String nome, String tituloDocenteCodigo, String tituloDocenteDescricao) {
        this.id = id;
        this.nome = nome;
        this.tituloDocenteCodigo = tituloDocenteCodigo;
        this.tituloDocenteDescricao = tituloDocenteDescricao;
    }

    public Professor(String nome, String tituloDocenteCodigo, String tituloDocenteDescricao) {
        this.nome = nome;
        this.tituloDocenteCodigo = tituloDocenteCodigo;
        this.tituloDocenteDescricao = tituloDocenteDescricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTituloDocenteCodigo() {
        return tituloDocenteCodigo;
    }

    public void setTituloDocenteCodigo(String tituloDocenteCodigo) {
        this.tituloDocenteCodigo = tituloDocenteCodigo;
    }

    public String getTituloDocenteDescricao() {
        return tituloDocenteDescricao;
    }

    public void setTituloDocenteDescricao(String tituloDocenteDescricao) {
        this.tituloDocenteDescricao = tituloDocenteDescricao;
    }
}

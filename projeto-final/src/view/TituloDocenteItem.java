package view;

public class TituloDocenteItem {
    private final int id;
    private final String descricao;

    public TituloDocenteItem(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
package view;

public class DiaSemanaItem {
    private final int id;
    private final String descricao;

    public DiaSemanaItem(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
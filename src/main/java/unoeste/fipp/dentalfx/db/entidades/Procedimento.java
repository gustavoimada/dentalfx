package unoeste.fipp.dentalfx.db.entidades;

public class Procedimento {
    private int id;
    private String descricao;
    private double tempo;
    private double valor;

    public Procedimento(int id, String descricao, double tempo, double valor) {
        this.id = id;
        this.descricao = descricao;
        this.tempo = tempo;
        this.valor = valor;
    }

    public Procedimento(String descricao, double tempo, double valor) {
        this.descricao = descricao;
        this.tempo = tempo;
        this.valor = valor;
    }

    public Procedimento() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

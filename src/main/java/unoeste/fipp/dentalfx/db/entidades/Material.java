package unoeste.fipp.dentalfx.db.entidades;

public class Material {
    private int id;
    private String descricao;
    private double valor;

    public Material(int id, String descricao, double valor) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
    }

    public Material() {
        this(0,"",0);
    }

    public Material(String descricao, double valor) {
        this(0,descricao,valor);
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}

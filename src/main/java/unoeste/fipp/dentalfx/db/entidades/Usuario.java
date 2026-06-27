package unoeste.fipp.dentalfx.db.entidades;

public class Usuario extends Pessoa{
    private Double nivel;
    private String senha;

    public Usuario(int id, String nome, Double nivel, String senha) {
        super(id, nome);
        this.nivel = nivel;
        this.senha = senha;
    }

    public Usuario(String nome, Double nivel, String senha) {
        super(nome);
        this.nivel = nivel;
        this.senha = senha;
    }

    public Usuario() {
        super();
    }

    public Double getNivel() {
        return nivel;
    }

    public void setNivel(Double nivel) {
        this.nivel = nivel;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

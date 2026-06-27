package unoeste.fipp.dentalfx.db.entidades;

public class Dentista extends Pessoa{
    private String email, fone;
    private double cro;

    public Dentista(int id, String nome, String email, String fone, double cro) {
        super(id, nome);
        this.email = email;
        this.fone = fone;
        this.cro = cro;
    }

    public Dentista(String nome, String email, String fone, double cro) {
        super(nome);
        this.email = email;
        this.fone = fone;
        this.cro = cro;
    }

    public Dentista() {
        super();
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return this.getNome();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public double getCro() {
        return cro;
    }

    public void setCro(double cro) {
        this.cro = cro;
    }
}

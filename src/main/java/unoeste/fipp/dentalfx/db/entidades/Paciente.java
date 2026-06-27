package unoeste.fipp.dentalfx.db.entidades;

public class Paciente extends Pessoa{
    private String cpf, cep, cidade, bairro, numero, rua, uf, email, telefone, historico;

    public Paciente(int id, String nome, String cpf, String cep, String cidade, String bairro, String numero, String rua, String uf, String email, String telefone, String historico) {
        super(id, nome);
        this.cpf = cpf;
        this.cep = cep;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.rua = rua;
        this.uf = uf;
        this.email = email;
        this.telefone = telefone;
        this.historico = historico;
    }

    public Paciente(String nome, String cpf, String cep, String cidade, String bairro, String numero, String rua, String uf, String email, String telefone, String historico) {
        super(nome);
        this.cpf = cpf;
        this.cep = cep;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.rua = rua;
        this.uf = uf;
        this.email = email;
        this.telefone = telefone;
        this.historico = historico;
    }

    public Paciente() {
        super();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }
}

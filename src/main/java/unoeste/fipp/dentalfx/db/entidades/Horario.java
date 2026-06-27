// unoeste.fipp.dentalfx.db.entidades.Horario.java
package unoeste.fipp.dentalfx.db.entidades;

import java.time.LocalTime;

public class Horario {

    private int sequencia;
    private LocalTime hora;
    private Paciente paciente;
    private Atendimento atendimento;
    private int conId;

    public Horario(int sequencia, LocalTime hora) {
        this.sequencia = sequencia;
        this.hora = hora;
        this.paciente = null;
        this.atendimento = null;
        this.conId = 0;
    }

    public Horario(int sequencia, LocalTime hora, Paciente paciente, int conId) {
        this(sequencia, hora);
        this.paciente = paciente;
        this.conId = conId;
    }

    public int getSequencia() { return sequencia; }
    public void setSequencia(int sequencia) { this.sequencia = sequencia; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Atendimento getAtendimento() { return atendimento; }
    public void setAtendimento(Atendimento atendimento) { this.atendimento = atendimento; }

    public int getConId() { return conId; }
    public void setConId(int conId) { this.conId = conId; }
}
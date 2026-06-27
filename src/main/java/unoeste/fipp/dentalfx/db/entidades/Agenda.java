package unoeste.fipp.dentalfx.db.entidades;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Agenda {
    private Dentista dentista;
    private LocalDate data;
    private List<Horario> horarioList;

    private static final LocalTime[] HORAS_FIXAS = {
            LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
            LocalTime.of(12, 0), LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0),
            LocalTime.of(16, 0), LocalTime.of(17, 0)
    };

    public Agenda(Dentista dentista, LocalDate data) {
        this.dentista = dentista;
        this.data = data;
        horarioList = new ArrayList<>();

        for(int i = 0; i < HORAS_FIXAS.length; i++) {
            horarioList.add(new Horario(i + 1, HORAS_FIXAS[i]));
        }
    }

    public boolean setHorario(Horario horario) {
        if (horario.getSequencia() > 0 && horario.getSequencia() <= horarioList.size()) {
            horarioList.set(horario.getSequencia() - 1, horario);
            return true;
        }
        return false;
    }

    public Horario getHorario(int sequencia){
        if (sequencia > 0 && sequencia <= horarioList.size()) {
            return horarioList.get(sequencia - 1);
        }
        return null;
    }

    public Dentista getDentista() { return dentista; }
    public void setDentista(Dentista dentista) { this.dentista = dentista; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<Horario> getHorarioList() { return horarioList; }
}
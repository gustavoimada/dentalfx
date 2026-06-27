package unoeste.fipp.dentalfx.db.dals;

import unoeste.fipp.dentalfx.db.entidades.*;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AgendaDAL
{
    private final PessoaDAL dalPessoa = new PessoaDAL();

    public int agendar(Horario horario, Dentista dentista, LocalDate data) {

        int conId = getConId(dentista, data, horario.getHora());

        if (conId > 0)
        {
            String sql = "UPDATE consulta SET pac_id = ?, con_efetivado = FALSE, con_relato = NULL WHERE con_id = ?";
            try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
                ps.setInt(1, horario.getPaciente().getId());
                ps.setInt(2, conId);

                if (ps.executeUpdate() > 0) {
                    horario.setConId(conId);
                    return conId;
                }
                return 0;

            } catch (SQLException e) {
                System.out.println("Erro ao ATUALIZAR agendamento (ocupa): " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        }
        else // Se não existe, faz o insert
        {

            String sql = String.format(
                    "INSERT INTO consulta (pac_id, den_id, con_data, con_horario, con_efetivado) VALUES (%d, %d, '%s', %d, %s)",
                    horario.getPaciente().getId(),
                    dentista.getId(),
                    data.toString(),
                    horario.getHora().getHour(),
                    "FALSE"
            );

            if (SingletonDB.getConexao().manipular(sql)) {

                int novoConId = SingletonDB.getConexao().getMaxPK("consulta", "con_id");

                horario.setConId(novoConId);

                return novoConId;
            }
            return 0;
        }
    }

    private int getConId(Dentista dentista, LocalDate data, LocalTime hora) {
        String sql = "SELECT con_id FROM consulta WHERE den_id = ? AND con_data = ? AND con_horario = ?";
        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ps.setInt(1, dentista.getId());
            ps.setDate(2, Date.valueOf(data));
            ps.setInt(3, hora.getHour());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("con_id");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar ConId: " + e.getMessage());
        }
        return 0;
    }

    public boolean cancelar(int conId) {
        String sql = "UPDATE consulta SET pac_id = NULL, con_efetivado = FALSE, con_relato = NULL WHERE con_id = ?";

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ps.setInt(1, conId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERRO FATAL ao cancelar consulta: " + e.getMessage());
            return false;
        }
    }

    // retorna a AGENDA DO DIA (objeto Agenda com a lista de Horarios preenchida)
    public Agenda getPorDiaEDentista(Dentista dentista, LocalDate data) {

        Agenda agenda = new Agenda(dentista, data);

        String sql =
                "SELECT c.con_id, c.pac_id, c.con_horario FROM consulta c " +
                        "WHERE den_id = ? AND con_data = ? " +
                        "ORDER BY c.con_horario";

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {

            ps.setInt(1, dentista.getId());
            ps.setDate(2, Date.valueOf(data));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int conId = rs.getInt("con_id");
                LocalTime horaConsulta = LocalTime.of(rs.getInt("con_horario"), 0);

                Paciente pac = null;
                if (rs.getObject("pac_id") != null) {
                    int pacienteId = rs.getInt("pac_id");

                    List<Pessoa> pessoasEncontradas = dalPessoa.get(String.valueOf(pacienteId), new Paciente());

                    if (!pessoasEncontradas.isEmpty()) {
                        pac = (Paciente) pessoasEncontradas.get(0);
                    }
                }

                int sequencia = 0;
                for(int i = 0; i < agenda.getHorarioList().size(); i++) {
                    Horario h = agenda.getHorarioList().get(i);
                    if (h.getHora().equals(horaConsulta)) {
                        sequencia = i + 1;
                        break;
                    }
                }

                if (sequencia > 0 && pac != null) {
                    Horario ocupado = new Horario(sequencia, horaConsulta, pac, conId);
                    agenda.setHorario(ocupado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar agenda/consulta por dia e dentista: " + e.getMessage());
            e.printStackTrace();
        }

        return agenda;
    }
}
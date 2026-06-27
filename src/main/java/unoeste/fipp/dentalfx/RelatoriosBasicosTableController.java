package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.view.JasperViewer;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.ResultSet;

import static unoeste.fipp.dentalfx.RelatorioTableController.gerarRelatorio;

public class RelatoriosBasicosTableController {
    public Button btFechar;

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    @FXML
    void onRelMaterial(ActionEvent event)
    {
        gerarRelatorio("SELECT * FROM material ORDER BY mat_desc","MyReports/rel_materiais.jasper","Relatório de Materiais");
    }

    public void onRelDentista(ActionEvent actionEvent) {
        gerarRelatorio("SELECT * FROM dentista ORDER BY den_nome","MyReports/rel_dentista.jasper","Relatório de Dentista");
    }

    public void onRelProcedimento(ActionEvent actionEvent) {
        gerarRelatorio("SELECT * FROM procedimento ORDER BY pro_desc","MyReports/rel_proc.jasper","Relatório de Procedimento");
    }

    public void onRelPaciente(ActionEvent actionEvent) {
        gerarRelatorio("SELECT * FROM paciente ORDER BY pac_cidade, pac_nome","MyReports/rel_pacientes.jasper","Relatório de Pacientes");
    }

    public void onRelAgendaDia(ActionEvent actionEvent) {
        gerarRelatorio("SELECT c.con_horario AS horario, p.pac_nome, d.den_nome, CASE WHEN c.con_efetivado = TRUE THEN 'Efetivado' ELSE 'Agendado' END AS status FROM consulta c JOIN paciente p ON c.pac_id = p.pac_id JOIN dentista d ON c.den_id = d.den_id WHERE c.con_data = CURRENT_DATE ORDER BY horario", "MyReports/rel_agenda_dia.jasper", "Agenda do Dia");
    }
}

package unoeste.fipp.dentalfx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import unoeste.fipp.dentalfx.db.entidades.Dentista;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static unoeste.fipp.dentalfx.RelatorioTableController.gerarRelatorio;

public class RelatoriosAnaliticosFormController
{

    @FXML
    private DatePicker dpDataInicial;
    @FXML
    private DatePicker dpDataFinal;

    @FXML
    private ComboBox<Dentista> cbDentista;

    @FXML
    public void initialize() {
        carregarDentistas();
    }

    private void carregarDentistas() {
        List<Dentista> dentistas = new ArrayList<>();
        dentistas.add(new Dentista(0, "Todos os Dentistas", "", "", 0.0));

        try {
            ResultSet rs = SingletonDB.getConexao().consultar("SELECT den_id, den_nome FROM dentista ORDER BY den_nome");
            while (rs.next()) {
                dentistas.add(new Dentista(rs.getInt("den_id"), rs.getString("den_nome"), "", "", 0.0));
            }
            cbDentista.setItems(FXCollections.observableArrayList(dentistas));
            cbDentista.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            System.err.println("Erro ao carregar dentistas: " + e.getMessage());
        }
    }

    @FXML
    void onGerarRelatorio(ActionEvent event) {
        String dataInicial = "";
        String dataFinal = "";
        int dentistaId = 0;

        if (dpDataInicial.getValue() != null) {
            dataInicial = dpDataInicial.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (dpDataFinal.getValue() != null) {
            dataFinal = dpDataFinal.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        if (cbDentista.getSelectionModel().getSelectedItem() != null) {
            dentistaId = cbDentista.getSelectionModel().getSelectedItem().getId();
        }

        StringBuilder whereClause = new StringBuilder("WHERE c.con_efetivado = TRUE ");

        if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
            whereClause.append("AND c.con_data BETWEEN '").append(dataInicial).append("' AND '").append(dataFinal).append("' ");
        } else if (!dataInicial.isEmpty()) {
            whereClause.append("AND c.con_data >= '").append(dataInicial).append("' ");
        } else if (!dataFinal.isEmpty()) {
            whereClause.append("AND c.con_data <= '").append(dataFinal).append("' ");
        }

        if (dentistaId > 0) {
            whereClause.append("AND c.den_id = ").append(dentistaId).append(" ");
        }

        String sql = "SELECT " +
                "c.con_data AS data, " +
                "TO_CHAR(c.con_horario * interval '1 hour', 'HH24:MI') AS horario, " +
                "p.pac_nome, " +
                "d.den_nome AS dentista, " +
                "p.pac_cidade " +
                "FROM consulta c " +
                "JOIN paciente p ON c.pac_id = p.pac_id " +
                "JOIN dentista d ON c.den_id = d.den_id " +
                whereClause.toString() +
                "ORDER BY c.con_data, horario";

        String titulo = "Relatório Analítico de Atendimentos";

        System.out.println("SQL Gerado: " + sql);

        gerarRelatorio(sql, "MyReports/rel_analiticoAt.jasper", titulo);
    }
}
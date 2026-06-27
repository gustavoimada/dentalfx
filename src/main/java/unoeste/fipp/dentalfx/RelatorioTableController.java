package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.view.JasperViewer;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.io.IOException;
import java.sql.ResultSet;

public class RelatorioTableController{

    public Button btFechar;

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    public void onAbrirBasicos(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("relatorios-basicos-table-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Relatorios Basicos");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void onAbrirAnaliticos(ActionEvent actionEvent) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("relatorios-analiticos-table-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Relatorios Analíticos");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public static void gerarRelatorio(String sql, String relat, String titulo) {
        try {
            //sql para obter os dados para o relatorio
            ResultSet rs = SingletonDB.getConexao().consultar(sql);
            //implementação da interface JRDataSource para DataSource ResultSet
            JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
            //chamando o relatório
            String jasperPrint = JasperFillManager.fillReportToFile(relat,null, jrRS);
            JasperViewer viewer = new JasperViewer(jasperPrint, false, false);
            viewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH);//maximizado
            viewer.setTitle(titulo);//titulo do relatório
            viewer.setVisible(true);
        }
        catch (Exception erro) {
            erro.printStackTrace(); }
    }
}

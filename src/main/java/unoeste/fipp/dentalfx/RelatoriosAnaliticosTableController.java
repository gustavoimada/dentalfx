package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class RelatoriosAnaliticosTableController
{
    public Button btFechar;

    public void onRelAtendimentos (ActionEvent actionEvent) throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("relatorios-analiticos-form-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Relatorios Analíticos Formulário");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void onFecharSistema(ActionEvent actionEvent) {
        btFechar.getScene().getWindow().hide();
    }
}

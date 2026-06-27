package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AjudaController {
    public Button btFechar;
    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    public void onSobre(ActionEvent actionEvent) throws Exception
    {
            FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("sobre-view.fxml"));
            Stage stage=new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Sobre");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
    }

    public void onTopicos(ActionEvent actionEvent) throws Exception
    {
        // opção "super facil" q ele tinha falado
        //File doc = new File("ajuda/main.html");
        //Desktop.getDesktop().browse(doc.toURI());

        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("help-adm-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ajuda DentalFX ADM");
        stage.setScene(scene);
        stage.showAndWait();
    }
}

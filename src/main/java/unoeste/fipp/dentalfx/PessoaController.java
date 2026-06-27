package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Node;

import java.io.IOException;

public class PessoaController {

    public Button btFechar;

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    @FXML
    void onAbrirCadastroDentista(ActionEvent event) throws Exception{
            FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("dentista-table-view.fxml"));
            Stage stage=new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Cadastro de dentista");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
    }

    @FXML
    void onAbrirCadastroPaciente(ActionEvent event) throws Exception{
            FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("paciente-table-view.fxml"));
            Stage stage=new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Cadastro de paciente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
            //carregarTabela("");
    }

    @FXML
    void onAbrirCadastroUsuario(ActionEvent event) throws Exception{
            FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("usuario-table-view.fxml"));
            Stage stage=new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Cadastro de usuário");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            //carregarTabela("");
    }
}

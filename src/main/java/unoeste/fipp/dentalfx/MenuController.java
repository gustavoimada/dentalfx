package unoeste.fipp.dentalfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.stage.Window;
import unoeste.fipp.dentalfx.db.util.SingletonDB;
import unoeste.fipp.dentalfx.utils.Seguranca;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuController
{
    public Button btFechar;
    public Button btBackup;
    public Button btRestore;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public static double retornarNivel(String usuario, String senha) throws SQLException {

        String sql = "SELECT uso_nivel FROM usuario WHERE uso_nome = ? AND uso_senha = ?";

        try (PreparedStatement ps = SingletonDB.getConexao().getPreparedStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, senha);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("uso_nivel");
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar usuário: " + e.getMessage());
            throw new SQLException("Erro de banco de dados ao fazer login.");
        }
        return 0;
    }

    public void onAbrirCadastroPessoa(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("pessoa-view.fxml"));
        Window ownerWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cadastro de pessoas");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void onAbrirCadastroProcedimento(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("procedimento-table-view.fxml"));
        Window ownerWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cadastro de procedimento");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void onAbrirCadastroMaterial(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("material-table-view.fxml"));
        Window ownerWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cadastro de material");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void onAbrirRelatorios(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("relatorios-table-view.fxml"));
        Window ownerWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Relatorios");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    public void onBackup(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Arquivo de Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("sql file", "*.sql"));
        File bkpFile = fileChooser.showSaveDialog(null);
        if (bkpFile != null) {
            try {
                Seguranca.backup(bkpFile.getAbsolutePath(), "sisdentaldb");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onRestore(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Arquivo para Restaurar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("sql file", "*.sql"));
        File bkpFile = fileChooser.showOpenDialog(null);
        if (bkpFile != null) {
            try {
                Seguranca.restaurar(bkpFile.getAbsolutePath(), "sisdentaldb");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onAjuda(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("ajuda-view.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Menu De Ajuda ADM");
        stage.setScene(scene);
        stage.showAndWait();
    }
}
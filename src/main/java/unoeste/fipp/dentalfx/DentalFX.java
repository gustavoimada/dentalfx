package unoeste.fipp.dentalfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class DentalFX extends Application
{
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        loadLoginScreen();

        if (primaryStage != null) {
            primaryStage.show();
        }
    }

    private void loadLoginScreen() {
        if (primaryStage == null) return;

        primaryStage.setTitle("Login");
        AnchorPane pane = new AnchorPane();

        Label lUsuario = new Label("Usuario:");
        lUsuario.setLayoutX(10); lUsuario.setLayoutY(10);
        Label lSenha = new Label("Senha:");
        lSenha.setLayoutX(10); lSenha.setLayoutY(50);

        TextField tfUsuario = new TextField();
        tfUsuario.setPromptText("Entre com o usuário...");
        tfUsuario.setLayoutX(100); tfUsuario.setLayoutY(10);

        PasswordField pfSenha = new PasswordField();
        pfSenha.setPromptText("Entre com a senha...");
        pfSenha.setLayoutX(100); pfSenha.setLayoutY(50);

        Button bEntrar = new Button("Entrar...");
        bEntrar.setLayoutX(100); bEntrar.setLayoutY(100);

        Button bSair = new Button("Sair...");
        bSair.setLayoutX(180); bSair.setLayoutY(100);

        primaryStage.setOnCloseRequest(e -> Platform.exit());
        bSair.setOnAction(e -> Platform.exit());

        pane.getChildren().addAll(lUsuario, tfUsuario, lSenha, pfSenha, bEntrar, bSair);

        Scene scene = new Scene(pane, 300, 150);
        primaryStage.setScene(scene);

        bEntrar.setOnAction(event ->
        {
            String usuario = tfUsuario.getText();
            String senha = pfSenha.getText();
            String fxml = "";

            if (usuario.isEmpty() || senha.isEmpty())
            {
                new Alert(Alert.AlertType.WARNING, "Usuário e senha são obrigatórios!").showAndWait();
            }
            else
            {
                try
                {
                    double nivel = MenuController.retornarNivel(usuario, senha);

                    if (nivel == 1)
                    {
                        fxml = "menu-view.fxml";
                        primaryStage.setTitle("DentalFX ADM");
                    } else if (nivel == 2)
                    {
                        fxml = "agendamento-view.fxml";
                        primaryStage.setTitle("DentalFX Secretária");
                    } else if (nivel == 3)
                    {
                        fxml = "atendimento-view.fxml";
                        primaryStage.setTitle("DentalFX Dentista");
                    } else
                    {
                        new Alert(Alert.AlertType.ERROR, "Usuário ou senha inválidos.").showAndWait();
                        return;
                    }

                    FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource(fxml));
                    Scene nextScene = new Scene(fxmlLoader.load());

                    // faz isso apenas pro nivel 3 que eh a tela do dentista
                    // para que apenas os dados desse usuario (dentista), sejam alterados/consultados
                    if (nivel == 3)
                    {
                        AtendimentoViewController controller = fxmlLoader.getController();
                        controller.setUsuarioLogado(usuario);
                    }

                    // pra voltar para tela de login
                    primaryStage.setOnCloseRequest(e -> {
                        e.consume();

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja encerrar a sessão?", ButtonType.YES, ButtonType.NO);
                        alert.setTitle("Encerrar Sessão");
                        alert.setHeaderText(null);

                        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                            loadLoginScreen();
                        }
                    });
                    primaryStage.setScene(nextScene);
                    primaryStage.centerOnScreen();

                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erro ao processar login: " + e.getMessage()).showAndWait();
                }
            }
        });
    }


    public static void main(String[] args) {
        if (SingletonDB.conectar()) {
            launch();
        } else {
            int op = JOptionPane.showConfirmDialog(null, "Erro ao conectar:\n" +
                    SingletonDB.getConexao().getMensagemErro() +
                    "\n Deseja criar a base de dados?");
            if (op == JOptionPane.YES_OPTION) {
                if (SingletonDB.criarDatabase(SingletonDB.getDatabaseName(), SingletonDB.getUser(), SingletonDB.getPassword())) {
                    System.out.println("Database criado com sucesso");
                    SingletonDB.criarTabelas("sisdentaldb.sql", SingletonDB.getDatabaseName());
                } else {
                    System.out.println("Erro ao criar");
                }
            }
            Platform.exit();
        }
    }
}

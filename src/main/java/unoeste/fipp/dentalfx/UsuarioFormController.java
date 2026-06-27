package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

public class UsuarioFormController implements Initializable {

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfNivel;

    @FXML
    private TextField tfNome;

    @FXML
    private TextField tfSenha;

    private Usuario usuarioEmEdicao;

    @FXML
    void onCancelar(ActionEvent event) {
        tfNivel.getScene().getWindow().hide();
        UsuarioTableController.usuario = null;
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        PessoaDAL dal=new PessoaDAL();

        double nivelValue = 0.0;
        try {
            nivelValue = Double.parseDouble(tfNivel.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "O valor do Nível é inválido ou está vazio.").showAndWait();
            return;
        }

        Usuario usuario = (this.usuarioEmEdicao != null) ? this.usuarioEmEdicao : new Usuario();

        usuario.setNome(tfNome.getText());
        usuario.setNivel(nivelValue);
        usuario.setSenha(tfSenha.getText());

        boolean sucesso = false;

        if (usuario.getId() > 0) {
            sucesso = dal.alterar(usuario);
            if(!sucesso){
                new Alert(Alert.AlertType.ERROR, "Erro ao alterar o Usuário.").showAndWait();
                return;
            }
        }
        else {
            sucesso = dal.gravar(usuario);
            if(!sucesso){
                new Alert(Alert.AlertType.ERROR, "Erro ao gravar o novo Usuário.").showAndWait();
                return;
            }
        }

        tfNome.getScene().getWindow().hide();
        UsuarioTableController.usuario = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfID.setDisable(true);

        Platform.runLater(()->{
            tfNome.requestFocus();

            if(UsuarioTableController.usuario instanceof Usuario usuarioParaAlterar)
            {
                this.usuarioEmEdicao = usuarioParaAlterar;

                tfID.setText(""+usuarioParaAlterar.getId());
                tfNome.setText(usuarioParaAlterar.getNome());
                tfNivel.setText(String.valueOf(usuarioParaAlterar.getNivel()));
                tfSenha.setText(usuarioParaAlterar.getSenha());

                UsuarioTableController.usuario = null;
            }
        });
    }
}
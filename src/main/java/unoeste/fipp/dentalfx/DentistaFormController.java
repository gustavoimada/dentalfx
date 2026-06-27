package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.Dentista;
import unoeste.fipp.dentalfx.utils.MaskFieldUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class DentistaFormController implements Initializable {

    @FXML private TextField tfCro;
    @FXML private TextField tfEmail;
    @FXML private TextField tfFone;
    @FXML private TextField tfID;
    @FXML private TextField tfNome;

    private Dentista dentistaEmEdicao;

    @FXML
    void onCancelar(ActionEvent event) {
        tfID.getScene().getWindow().hide();
        // Limpa a referência estática
        DentistaTableController.dentista = null;
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        PessoaDAL dal=new PessoaDAL();

        double croValue = 0.0;
        try {
            // Garante o formato Double.
            croValue = Double.parseDouble(tfCro.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "O valor do CRO é inválido ou está vazio.").showAndWait();
            return;
        }

        Dentista dentista = (this.dentistaEmEdicao != null) ? this.dentistaEmEdicao : new Dentista();

        dentista.setNome(tfNome.getText());
        dentista.setEmail(tfEmail.getText());
        dentista.setFone(tfFone.getText());
        dentista.setCro(croValue);

        boolean sucesso = false;

        if (dentista.getId() > 0) {
            sucesso = dal.alterar(dentista);
            if (!sucesso) {
                new Alert(Alert.AlertType.ERROR, "Erro ao alterar o Dentista. Verifique o console para detalhes do SQL.").showAndWait();
                return;
            }
        }
        else {
            sucesso = dal.gravar(dentista);
            if (!sucesso) {
                new Alert(Alert.AlertType.ERROR, "Erro ao gravar o novo Dentista.").showAndWait();
                return;
            }
        }

        tfNome.getScene().getWindow().hide();
        DentistaTableController.dentista = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MaskFieldUtil.foneField(tfFone);
        MaskFieldUtil.numericField(tfCro);

        tfID.setDisable(true);

        Platform.runLater(()->{
            tfNome.requestFocus();

            Dentista dentistaParaAlterar = DentistaTableController.dentista;

            if(dentistaParaAlterar != null)
            {
                this.dentistaEmEdicao = dentistaParaAlterar;

                tfID.setText(""+dentistaParaAlterar.getId());
                tfNome.setText(dentistaParaAlterar.getNome());
                tfEmail.setText(dentistaParaAlterar.getEmail());
                tfFone.setText(dentistaParaAlterar.getFone());
                tfCro.setText(String.valueOf(dentistaParaAlterar.getCro()));
            }
        });
    }
}
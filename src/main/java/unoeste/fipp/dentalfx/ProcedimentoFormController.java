package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import unoeste.fipp.dentalfx.db.dals.MaterialDAL;
import unoeste.fipp.dentalfx.db.dals.ProcedimentoDAL;
import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.db.entidades.Procedimento;
import unoeste.fipp.dentalfx.utils.MaskFieldUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ProcedimentoFormController implements Initializable {

    @FXML
    private TextField tfDesc;

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfPreco;

    @FXML
    private TextField tfTempo;

    @FXML
    void onCancelar(ActionEvent event) {
        tfPreco.getScene().getWindow().hide();

    }

    @FXML
    void onConfirmar(ActionEvent event)
    {
        ProcedimentoDAL dal=new ProcedimentoDAL();
        Procedimento procedimento=new Procedimento(tfDesc.getText(),Double.parseDouble(tfTempo.getText()),Double.parseDouble(tfPreco.getText().replace(",",".")));
        if(!tfID.getText().isEmpty()){
            //é uma alteração
            procedimento.setId(Integer.parseInt(tfID.getText()));
            if(!dal.alterar(procedimento))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erro ao alterar o procedimento");
                alert.showAndWait();
            }
        }
        else {
            //um novo material
            if(!dal.gravar(procedimento))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Erro ao gravar o procedimento");
                alert.showAndWait();
            }
        }
        tfPreco.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MaskFieldUtil.monetaryField(tfPreco);
        Platform.runLater(()->{tfDesc.requestFocus();});
        if(ProcedimentoTableController.procedimento!=null)
        {
            Procedimento procedimento=ProcedimentoTableController.procedimento;
            tfID.setText(""+procedimento.getId());
            tfDesc.setText(procedimento.getDescricao());
            tfTempo.setText(String.format("%.2f", procedimento.getTempo()));
            tfPreco.setText(String.format("%.2f",procedimento.getValor()));
        }
    }

}

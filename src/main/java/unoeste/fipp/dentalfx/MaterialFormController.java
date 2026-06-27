package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import unoeste.fipp.dentalfx.db.dals.MaterialDAL;
import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.utils.MaskFieldUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class MaterialFormController implements Initializable {

    @FXML
    private TextField tfDesc;

    @FXML
    private TextField tfID;

    @FXML
    private TextField tfPreco;

    @FXML
    void onCancelar(ActionEvent event) {
        tfPreco.getScene().getWindow().hide();
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        MaterialDAL dal=new MaterialDAL();
        Material material=new Material(tfDesc.getText(),Double.parseDouble(tfPreco.getText().replace(",",".")));
        if(!tfID.getText().isEmpty()){
            //é uma alteração
            material.setId(Integer.parseInt(tfID.getText()));
            dal.alterar(material);
        }
        else {
            //um novo material
            //verificar se deu certo para gravar
            dal.gravar(material);
        }
        tfPreco.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MaskFieldUtil.monetaryField(tfPreco);
        Platform.runLater(()->{tfDesc.requestFocus();});
        if(MaterialTableController.material!=null)
        {
            Material material=MaterialTableController.material;
            tfID.setText(""+material.getId());
            tfDesc.setText(material.getDescricao());
            tfPreco.setText(String.format("%.2f",material.getValor()));
        }
    }
}

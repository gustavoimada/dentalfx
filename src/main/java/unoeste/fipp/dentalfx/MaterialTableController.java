package unoeste.fipp.dentalfx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import unoeste.fipp.dentalfx.db.dals.MaterialDAL;
import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MaterialTableController implements Initializable {

    @FXML
    private TableView<Material> tableView;

    @FXML
    private TableColumn<Material, String> colDesc;

    @FXML
    private TableColumn<Material, Integer> colID;

    @FXML
    private TableColumn<Material, Double> colPreco;

    @FXML
    private TextField tfFiltro;

    public static Material material=null;

    @FXML
    void btFechar(ActionEvent event) {
        tfFiltro.getScene().getWindow().hide();
    }

    @FXML
    void onAlterar(ActionEvent event) {
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            material= tableView.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("material-form-view.fxml"));
                Stage stage=new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Alterar Material");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                carregarTabela("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        material=null;
    }

    @FXML
    void onApagar(ActionEvent event) {
        MaterialDAL dal=new MaterialDAL();
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            //perguntar se deseja realmente apagar
            if(!dal.apagar(tableView.getSelectionModel().getSelectedItem()))
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro ao apagar o material");
                alert.setContentText(SingletonDB.getConexao().getMensagemErro());
                alert.showAndWait();
            }
            carregarTabela("");
        }
    }

    @FXML
    void onFiltrar(KeyEvent event) {
        carregarTabela("mat_desc LIKE '%"+tfFiltro.getText()+"%'");
    }

    @FXML
    void onNovoMaterial(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("material-form-view.fxml"));
            Stage stage=new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Novo Material");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            carregarTabela("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("valor"));
        carregarTabela("");
    }

    private void carregarTabela(String filtro) {
        MaterialDAL dal=new MaterialDAL();
        List<Material> materialList=dal.get(filtro);
        tableView.setItems(FXCollections.observableArrayList(materialList));
    }
}

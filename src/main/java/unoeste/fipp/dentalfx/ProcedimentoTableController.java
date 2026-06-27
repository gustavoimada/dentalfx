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
import unoeste.fipp.dentalfx.db.dals.ProcedimentoDAL;
import unoeste.fipp.dentalfx.db.entidades.Material;
import unoeste.fipp.dentalfx.db.entidades.Procedimento;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProcedimentoTableController implements Initializable {

    @FXML
    private TableColumn<Procedimento, String> colNome;

    @FXML
    private TableColumn<Procedimento, Integer> colID;

    @FXML
    private TableColumn<Procedimento, Double> colTempo;

    @FXML
    private TableColumn<Procedimento, Double> colValor;

    @FXML
    private TableView<Procedimento> tableView;

    @FXML
    private TextField tfFiltro;

    public static Procedimento procedimento = null;

    @FXML
    void btFechar(ActionEvent event) {
        tfFiltro.getScene().getWindow().hide();
    }

    @FXML
    void onNovoProcedimento(ActionEvent event) throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("procedimento-form-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Novo Procedimento");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        carregarTabela("");
    }

    @FXML
    void onAlterar(ActionEvent event) {
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            procedimento = tableView.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("procedimento-form-view.fxml"));
                Stage stage=new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Alterar Procedimento");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                carregarTabela("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        procedimento=null;
    }

    @FXML
    void onApagar(ActionEvent event) {
        ProcedimentoDAL dal=new ProcedimentoDAL();
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            //perguntar se deseja realmente apagar
            if(!dal.apagar(tableView.getSelectionModel().getSelectedItem()))
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro ao apagar o procedimento");
                alert.setContentText(SingletonDB.getConexao().getMensagemErro());
                alert.showAndWait();
            }
            carregarTabela("");
        }
    }

    @FXML
    void onFiltrar(KeyEvent event) {
        carregarTabela("pro_desc LIKE '%"+tfFiltro.getText()+"%'");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTempo.setCellValueFactory(new PropertyValueFactory<>("tempo"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        carregarTabela("");
    }

    private void carregarTabela(String filtro) {
        ProcedimentoDAL dal=new ProcedimentoDAL();
        List<Procedimento> procedimentoList=dal.get(filtro);
        tableView.setItems(FXCollections.observableArrayList(procedimentoList));
    }

}

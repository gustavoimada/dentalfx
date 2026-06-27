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
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.Pessoa;
import unoeste.fipp.dentalfx.db.entidades.Usuario;
import unoeste.fipp.dentalfx.db.util.SingletonDB;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsuarioTableController implements Initializable {

    @FXML
    private TableColumn<Pessoa, Integer> colID;

    @FXML
    private TableColumn<Pessoa, Double> colNivel;

    @FXML
    private TableColumn<Pessoa, String> colNome;

    @FXML
    private TableView<Pessoa> tableView;

    @FXML
    private TextField tfFiltro;

    public static Pessoa usuario = null;

    @FXML
    void btFechar(ActionEvent event) {
        tfFiltro.getScene().getWindow().hide();
    }

    @FXML
    void onAlterar(ActionEvent event) {
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            usuario = tableView.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("usuario-form-view.fxml"));
                Stage stage=new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Alterar Usuário");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                carregarTabela("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        usuario=null;
    }

    @FXML
    void onApagar(ActionEvent event) {
        PessoaDAL dal=new PessoaDAL();
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            if(!dal.apagar(tableView.getSelectionModel().getSelectedItem()))
            {
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro ao apagar o usuário");
                alert.setContentText(SingletonDB.getConexao().getMensagemErro());
                alert.showAndWait();
            }
            carregarTabela("");
        }
    }

    @FXML
    void onFiltrar(KeyEvent event) {
        carregarTabela(tfFiltro.getText());
    }

    @FXML
    void onNovoUsuario(ActionEvent event) throws Exception{
        usuario = null;
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("usuario-form-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Novo usuário");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        carregarTabela("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNivel.setCellValueFactory(new PropertyValueFactory<>("nivel"));
        carregarTabela("");
    }

    private void carregarTabela(String filtro) {
        PessoaDAL dal=new PessoaDAL();
        List<Pessoa> pessoaList=dal.get(filtro,new Usuario());
        tableView.setItems(FXCollections.observableArrayList(pessoaList));
    }
}
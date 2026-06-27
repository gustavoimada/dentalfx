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
import unoeste.fipp.dentalfx.db.entidades.Dentista;
import unoeste.fipp.dentalfx.db.entidades.Pessoa;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DentistaTableController implements Initializable {

    @FXML private TableColumn<Dentista, Integer> colID;
    @FXML private TableColumn<Dentista, String> colNome;
    @FXML private TableColumn<Dentista, Double> colCro;
    @FXML private TableColumn<Dentista, String> colFone;
    @FXML private TableView<Dentista> tableView;
    @FXML private TextField tfFiltro;

    public static Dentista dentista = null;

    @FXML
    void btFechar(ActionEvent event) {
        tfFiltro.getScene().getWindow().hide();
    }

    @FXML
    void onNovoDentista(ActionEvent event) throws Exception
    {
        DentistaTableController.dentista = null; // Garante que é um NOVO registro
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("dentista-form-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Novo Dentista");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        carregarTabela("");
    }

    @FXML
    void onAlterar(ActionEvent event) {
        if(tableView.getSelectionModel().getSelectedIndex()!=-1) {
            DentistaTableController.dentista = tableView.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("dentista-form-view.fxml"));
                Stage stage=new Stage();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setTitle("Alterar Dentista");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                carregarTabela("");
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Erro ao carregar formulário de alteração: " + e.getMessage()).showAndWait();
            }
        }
    }

    @FXML
    void onApagar(ActionEvent event) {
        PessoaDAL dal=new PessoaDAL();
        Dentista selecionado = tableView.getSelectionModel().getSelectedItem();

        if(selecionado != null) {
            if(!dal.apagar(selecionado))
            {
                new Alert(Alert.AlertType.ERROR, "Erro ao apagar o dentista.").showAndWait();
            }
            carregarTabela("");
        }
    }

    @FXML
    void onFiltrar(KeyEvent event) {
        carregarTabela(tfFiltro.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCro.setCellValueFactory(new PropertyValueFactory<>("cro"));
        colFone.setCellValueFactory(new PropertyValueFactory<>("fone"));
        carregarTabela("");
    }

    private void carregarTabela(String filtro) {
        PessoaDAL dal=new PessoaDAL();
        List<Pessoa> pessoasList = dal.get(filtro, new Dentista());

        List<Dentista> dentistaList = pessoasList.stream()
                .filter(p -> p instanceof Dentista)
                .map(p -> (Dentista) p)
                .toList();

        tableView.setItems(FXCollections.observableArrayList(dentistaList));
    }
}
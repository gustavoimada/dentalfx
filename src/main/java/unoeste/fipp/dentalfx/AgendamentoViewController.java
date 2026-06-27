package unoeste.fipp.dentalfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import unoeste.fipp.dentalfx.db.dals.AgendaDAL;
import unoeste.fipp.dentalfx.db.dals.AtendimentoDAL;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AgendamentoViewController implements Initializable
{
    public Button btFechar;
    @FXML private DatePicker dpDiaConsulta;
    @FXML private ComboBox<Dentista> cbDentista;
    @FXML private TableView<Horario> tableView;
    @FXML private TableColumn<Horario, String> colHora;
    @FXML private TableColumn<Horario, String> colPaciente;
    @FXML private TableColumn<Horario, String> colStatus;

    private final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private Agenda agendaDoDia;
    private ObservableList<Horario> obsHorarios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        obsHorarios = FXCollections.observableArrayList();
        tableView.setItems(obsHorarios);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        colHora.setCellValueFactory(cell -> {
            LocalTime h = cell.getValue() != null ? cell.getValue().getHora() : null;
            String txt = (h == null) ? "" : h.format(HORA_FMT);
            return new SimpleStringProperty(txt);
        });

        colPaciente.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue() != null && cell.getValue().getPaciente() != null
                                ? cell.getValue().getPaciente().getNome()
                                : "LIVRE"
                )
        );

        colStatus.setCellValueFactory(cell -> {
            Horario horario = cell.getValue();
            if (horario == null || horario.getPaciente() == null) {
                return new SimpleStringProperty("LIVRE");
            }

            boolean realizado = new AtendimentoDAL().existeAtendimento(horario.getConId());

            if (realizado) {
                return new SimpleStringProperty("Realizado");
            } else {
                return new SimpleStringProperty("Agendado");
            }
        });

        tableView.setRowFactory(tv -> new TableRow<Horario>() {
            @Override
            protected void updateItem(Horario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    String status = colStatus.getCellData(item);
                    switch (status) {
                        case "LIVRE":
                            setStyle("-fx-background-color: #d4edda; -fx-font-weight: bold;");
                            break;
                        case "Agendado":
                            setStyle("-fx-background-color: #f8d7da;");
                            break;
                        case "Realizado":
                            setStyle("-fx-background-color: #cce5ff;");
                            break;
                        default: setStyle("");
                    }
                }
            }
        });

        List<Pessoa> dentistasPessoa = new PessoaDAL().get("", new Dentista());
        List<Dentista> dentistas = dentistasPessoa.stream().map(d -> (Dentista) d).collect(Collectors.toList());
        cbDentista.setItems(FXCollections.observableArrayList(dentistas));

        cbDentista.setConverter(new StringConverter<Dentista>() {
            @Override public String toString(Dentista d) { return d == null ? "" : d.getNome(); }
            @Override public Dentista fromString(String s) { return null; }
        });

        cbDentista.valueProperty().addListener((obs, oldV, newV) -> carregarAgenda());
        dpDiaConsulta.valueProperty().addListener((obs, oldV, newV) -> carregarAgenda());

        dpDiaConsulta.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    if (item.getDayOfWeek().getValue() == 6 || item.getDayOfWeek().getValue() == 7) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffcccc;");
                    }
                }
            }
        });
    }

    @FXML void onTrocouData(ActionEvent event) { carregarAgenda(); }
    @FXML void onTrocouDentista(ActionEvent event) { carregarAgenda(); }

    private void carregarAgenda() {
        if (cbDentista.getValue() == null || dpDiaConsulta.getValue() == null) {
            obsHorarios.clear();
            agendaDoDia = null;
            return;
        }

        Dentista dentista = cbDentista.getValue();
        LocalDate data = dpDiaConsulta.getValue();

        AgendaDAL dal = new AgendaDAL();
        agendaDoDia = dal.getPorDiaEDentista(dentista, data);

        obsHorarios.clear();
        obsHorarios.addAll(agendaDoDia.getHorarioList());
    }

    @FXML
    void onAgendar(ActionEvent event) {
        Horario selecionado = tableView.getSelectionModel().getSelectedItem();

        if (selecionado == null || selecionado.getPaciente() != null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um horário LIVRE.").showAndWait();
            return;
        }

        if (agendaDoDia == null || agendaDoDia.getDentista() == null || agendaDoDia.getData() == null) {
            new Alert(Alert.AlertType.ERROR, "Agenda do dia não carregada corretamente.").showAndWait();
            return;
        }

        Paciente pac = selecionarPacienteDialog();
        if (pac == null) return;

        Horario novoHorario = new Horario(
                selecionado.getSequencia(),
                selecionado.getHora(),
                pac,
                0
        );

        int conIdGerado = new AgendaDAL().agendar(novoHorario, agendaDoDia.getDentista(), agendaDoDia.getData());

        if (conIdGerado > 0) {

            int index = obsHorarios.indexOf(selecionado);

            if (index != -1) {
                obsHorarios.set(index, novoHorario);
            } else {
                carregarAgenda();
            }
            new Alert(Alert.AlertType.INFORMATION, "Consulta agendada!").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, "Erro ao agendar.").showAndWait();
        }
    }

    @FXML
    void onCancelarAgendamento(ActionEvent event) {
        Horario selecionado = tableView.getSelectionModel().getSelectedItem();

        if (selecionado == null || selecionado.getPaciente() == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma consulta AGENDADA (com um paciente).").showAndWait();
            return;
        }

        if (selecionado.getConId() == 0) {
            new Alert(Alert.AlertType.ERROR, "ID da consulta não encontrado. Não é possível cancelar.").showAndWait();
            return;
        }

        boolean realizado = new AtendimentoDAL().existeAtendimento(selecionado.getConId());
        if (realizado) {
            new Alert(Alert.AlertType.WARNING, "Esta consulta já foi REALIZADA. O cancelamento direto não é permitido.").showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancelar consulta de " + (selecionado.getPaciente() != null ? selecionado.getPaciente().getNome() : "") +
                        " em " + agendaDoDia.getData().format(DateTimeFormatter.ofPattern("dd/MM")) + " às " + selecionado.getHora().format(HORA_FMT) + "?",
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> res = alert.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {

            if (new AgendaDAL().cancelar(selecionado.getConId())) {

                Horario livre = new Horario(selecionado.getSequencia(), selecionado.getHora());

                int index = obsHorarios.indexOf(selecionado);
                if (index != -1) {
                    obsHorarios.set(index, livre);
                } else {
                    carregarAgenda();
                }

                new Alert(Alert.AlertType.INFORMATION, "Cancelamento e liberação do horário efetuados com sucesso!").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Erro ao cancelar. Verifique o log de erro da DAL.").showAndWait();
            }
        }
    }

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    private Paciente selecionarPacienteDialog() {
        Dialog<Paciente> dialog = new Dialog<>();
        dialog.setTitle("Selecionar Paciente");
        dialog.setHeaderText("Busque e escolha um paciente para o agendamento:");

        ButtonType btConfirmar = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btConfirmar, ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        TextField tfBusca = new TextField();
        tfBusca.setPromptText("Buscar por nome do paciente...");

        TableView<Paciente> tvPacientes = new TableView<>();

        TableColumn<Paciente, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNome()));

        TableColumn<Paciente, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTelefone()));

        tvPacientes.getColumns().setAll(colNome, colTelefone);
        tvPacientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        List<Pessoa> listaPessoas = new PessoaDAL().get("", new Paciente());
        List<Paciente> listaPacientes = listaPessoas.stream()
                .map(p -> (Paciente) p)
                .collect(Collectors.toList());

        ObservableList<Paciente> obsPacientes = FXCollections.observableArrayList(listaPacientes);
        FilteredList<Paciente> filteredPacientes = new FilteredList<>(obsPacientes, p -> true);

        tfBusca.textProperty().addListener((obs, oldV, newV) -> {
            filteredPacientes.setPredicate(paciente -> {
                if (newV == null || newV.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newV.toLowerCase();
                return paciente.getNome().toLowerCase().contains(lowerCaseFilter);
            });
        });

        tvPacientes.setItems(filteredPacientes);

        vbox.getChildren().addAll(tfBusca, tvPacientes);
        dialog.getDialogPane().setContent(vbox);

        dialog.getDialogPane().lookupButton(btConfirmar).setDisable(true);
        tvPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            dialog.getDialogPane().lookupButton(btConfirmar).setDisable(newS == null);
        });

        dialog.setResultConverter(btn -> {
            if (btn == btConfirmar) {
                return tvPacientes.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public void onTopicosSecretaria(ActionEvent actionEvent) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("help-secretaria-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ajuda DentalFX Secretaria");
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void onCadastrarSecretaria(ActionEvent actionEvent) throws Exception{
        Window ownerWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("agendamento-cadastros-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cadastro de Pessoas - Secretaria");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
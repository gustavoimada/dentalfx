package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import unoeste.fipp.dentalfx.db.dals.AgendaDAL;
import unoeste.fipp.dentalfx.db.dals.AtendimentoDAL;
import unoeste.fipp.dentalfx.db.dals.MaterialDAL;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.dals.ProcedimentoDAL;
import unoeste.fipp.dentalfx.db.entidades.*;
import unoeste.fipp.dentalfx.db.entidades.Atendimento.MatItem;
import unoeste.fipp.dentalfx.db.entidades.Atendimento.ProcItem;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AtendimentoViewController implements Initializable {

    public Button btFechar;
    @FXML private VBox vbAgenda;
    @FXML private DatePicker dpDiaConsulta;
    @FXML private TableView<Horario> tvAgendaDentista;
    @FXML private TableColumn<Horario, String> colHoraDentista;
    @FXML private TableColumn<Horario, String> colPacienteDentista;
    @FXML private TableColumn<Horario, String> colStatusDentista;
    @FXML private Button btIniciarAtendimento;

    @FXML private Label lbPacienteData;
    @FXML private TextArea taRelato;
    @FXML private ComboBox<Material> cbMaterial;
    @FXML private TableView<MatItem> tvMateriais;
    @FXML private TableColumn<MatItem, String> colMaterialNome;
    @FXML private TableColumn<MatItem, Integer> colMaterialQuant;
    @FXML private ComboBox<Procedimento> cbProcedimento;
    @FXML private TableView<Procedimento> tvProcedimentos;
    @FXML private TableColumn<Procedimento, String> colProcedimentoNome;
    @FXML private TableColumn<Procedimento, String> colProcedimentoQuant;

    private Agenda agendaDoDia;
    private Horario horarioSelecionado;
    private Atendimento atendimentoAtual;

    private Dentista dentistaLogado;
    private final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private List<MatItem> materiaisUtilizados = new ArrayList<>();
    private ObservableList<MatItem> obsMateriais;
    private ObservableList<Procedimento> obsProcedimentos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (colHoraDentista != null) {
            colHoraDentista.setCellValueFactory(cell -> {
                LocalTime h = cell.getValue() != null ? cell.getValue().getHora() : null;
                return new SimpleStringProperty(h == null ? "" : h.format(HORA_FMT));
            });
            colPacienteDentista.setCellValueFactory(cell ->
                    new SimpleStringProperty(
                            cell.getValue() != null && cell.getValue().getPaciente() != null
                                    ? cell.getValue().getPaciente().getNome()
                                    : "LIVRE"
                    )
            );
            colStatusDentista.setCellValueFactory(cell -> {
                Horario horario = cell.getValue();
                if (horario == null || horario.getPaciente() == null) return new SimpleStringProperty("LIVRE");

                boolean realizado = new AtendimentoDAL().existeAtendimento(horario.getConId());

                if (realizado) return new SimpleStringProperty("Realizado");
                return new SimpleStringProperty("Agendado");
            });

            dpDiaConsulta.valueProperty().addListener((obs, oldV, newV) -> carregarAgendaDentista());
        }

        colMaterialNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().material().getDescricao()));
        colMaterialQuant.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().quant()).asObject());
        tvMateriais.setEditable(true);
        colMaterialQuant.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override public String toString(Integer object) { return object == null ? "1" : object.toString(); }
            @Override public Integer fromString(String string) {
                try { return Integer.parseInt(string); } catch (NumberFormatException e) { return 1; }
            }
        }));
        colMaterialQuant.setOnEditCommit(event -> {
            MatItem oldItem = event.getRowValue();
            Material material = oldItem.material();
            int newQuant = event.getNewValue();

            if (newQuant > 0) {
                materiaisUtilizados.removeIf(item -> item.material().getId() == material.getId());
                materiaisUtilizados.add(new MatItem(material, newQuant));
            } else {
                materiaisUtilizados.removeIf(item -> item.material().getId() == material.getId());
            }
            tvMateriais.refresh();
        });

        colProcedimentoNome.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescricao()));
        colProcedimentoQuant.setCellValueFactory(cell -> new SimpleStringProperty("1"));


        cbMaterial.setItems(FXCollections.observableArrayList(new MaterialDAL().get("")));
        cbProcedimento.setItems(FXCollections.observableArrayList(new ProcedimentoDAL().get("")));

        cbMaterial.setConverter(new StringConverter<Material>() {
            @Override public String toString(Material m) { return m == null ? "" : m.getDescricao(); }
            @Override public Material fromString(String s) { return null; }
        });
        cbProcedimento.setConverter(new StringConverter<Procedimento>() {
            @Override public String toString(Procedimento p) { return p == null ? "" : p.getDescricao(); }
            @Override public Procedimento fromString(String s) { return null; }
        });

        obsMateriais = FXCollections.observableArrayList();
        tvMateriais.setItems(obsMateriais);

        obsProcedimentos = FXCollections.observableArrayList();
        tvProcedimentos.setItems(obsProcedimentos);

        if (taRelato != null) taRelato.getParent().setDisable(true);
    }

    @FXML
    void onFecharSistema(ActionEvent event) {
        btFechar.getScene().getWindow().hide();
    }

    private void carregarDadosAtendimento(Horario horario, Atendimento atendimento) {
        if (lbPacienteData != null) {
            lbPacienteData.setText(
                    horario.getPaciente().getNome() + " - " +
                            agendaDoDia.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " +
                            horario.getHora().format(HORA_FMT)
            );
        }

        taRelato.setText(atendimento.getRelato() != null ? atendimento.getRelato() : "");

        materiaisUtilizados.clear();
        if (atendimento.getMaterialList() != null) {
            materiaisUtilizados.addAll(atendimento.getMaterialList());
        }

        obsProcedimentos.clear();
        if (atendimento.getProcedimentoList() != null) {
            List<Procedimento> procs = atendimento.getProcedimentoList().stream()
                    .map(ProcItem::procedimento)
                    .collect(Collectors.toList());
            obsProcedimentos.addAll(procs);
        }

        obsMateriais.setAll(materiaisUtilizados);
    }

    public void setUsuarioLogado(String nomeUsuario) {
        this.dentistaLogado = new PessoaDAL().getDentistaPorNomeUsuario(nomeUsuario);

        if (this.dentistaLogado == null) {
            new Alert(Alert.AlertType.ERROR, "O usuário logado ('" + nomeUsuario + "') não está cadastrado como Dentista.").showAndWait();
            Platform.runLater(() -> {
                if (lbPacienteData != null && lbPacienteData.getScene() != null) {
                    ((Stage) lbPacienteData.getScene().getWindow()).close();
                }
            });
            return;
        } else {
            Platform.runLater(() -> {
                if (lbPacienteData != null) {
                    lbPacienteData.setText("Agenda do Dentista: " + this.dentistaLogado.getNome());
                }

                if (dpDiaConsulta != null) {
                    dpDiaConsulta.setValue(LocalDate.now());
                    carregarAgendaDentista();
                }
            });
        }
    }


    @FXML
    void onMoverParaAtendimento(ActionEvent event) {
        Horario selecionado = tvAgendaDentista.getSelectionModel().getSelectedItem(); // Seleciona Horario

        if (selecionado == null || selecionado.getPaciente() == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma consulta AGENDADA.").showAndWait();
            return;
        }
        if (agendaDoDia != null && agendaDoDia.getData().isAfter(LocalDate.now())) {
            new Alert(Alert.AlertType.WARNING, "Não é possível iniciar atendimento para consultas futuras (" + agendaDoDia.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ").").showAndWait();
            return;
        }

        if (selecionado.getConId() == 0) {
            new Alert(Alert.AlertType.ERROR, "ID da consulta não encontrado no slot. Não é possível iniciar o atendimento.").showAndWait();
            return;
        }

        if (new AtendimentoDAL().existeAtendimento(selecionado.getConId())) {
            new Alert(Alert.AlertType.WARNING, "Este atendimento já foi concluído e registrado.").showAndWait();
            return;
        }

        this.horarioSelecionado = selecionado;

        this.atendimentoAtual = new AtendimentoDAL().buscarPorAgenda(selecionado.getConId());

        if (this.atendimentoAtual == null) {
            this.atendimentoAtual = new Atendimento();
        }

        this.atendimentoAtual.setId(selecionado.getConId());

        this.atendimentoAtual.setAgenda(agendaDoDia);


        carregarDadosAtendimento(this.horarioSelecionado, this.atendimentoAtual);

        if (taRelato != null) taRelato.getParent().setDisable(false);
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        if (atendimentoAtual == null || taRelato.getText().trim().isEmpty() || atendimentoAtual.getId() <= 0) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma agenda e preencha o relato. Certifique-se de que a consulta tem um ID (conId).").showAndWait();
            return;
        }

        this.atendimentoAtual.setRelato(taRelato.getText());
        this.atendimentoAtual.setMaterialList(materiaisUtilizados);

        List<ProcItem> procItems = obsProcedimentos.stream()
                .map(p -> new ProcItem(p, 1))
                .collect(Collectors.toList());
        this.atendimentoAtual.setProcedimentoList(procItems);

        if (new AtendimentoDAL().gravar(this.atendimentoAtual)) {

            Horario atualizado = new Horario(horarioSelecionado.getSequencia(), horarioSelecionado.getHora(), horarioSelecionado.getPaciente(), horarioSelecionado.getConId());
            agendaDoDia.setHorario(atualizado);

            new Alert(Alert.AlertType.INFORMATION, "Atendimento registrado e agendamento concluído!").showAndWait();

            taRelato.getParent().setDisable(true);
            this.atendimentoAtual = null;
            this.horarioSelecionado = null;
            tvAgendaDentista.refresh();
        } else {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar o atendimento.").showAndWait();
        }
    }

    private void carregarAgendaDentista() {
        if (dentistaLogado == null || dpDiaConsulta.getValue() == null) {
            if (tvAgendaDentista != null) tvAgendaDentista.getItems().clear();
            agendaDoDia = null;
            return;
        }

        LocalDate data = dpDiaConsulta.getValue();
        AgendaDAL dal = new AgendaDAL();
        agendaDoDia = dal.getPorDiaEDentista(dentistaLogado, data);

        if (tvAgendaDentista != null) tvAgendaDentista.setItems(FXCollections.observableArrayList(agendaDoDia.getHorarioList()));
    }

    @FXML
    void onCancelar(ActionEvent event) {
        taRelato.getParent().setDisable(true);
        this.atendimentoAtual = null;
        this.horarioSelecionado = null;
        if (vbAgenda == null && taRelato.getScene() != null) {
            ((Stage) taRelato.getScene().getWindow()).close();
        }
    }

    @FXML
    public void onAddMaterial(ActionEvent actionEvent) {
        Material material = cbMaterial.getSelectionModel().getSelectedItem();
        if (material != null) {
            Optional<MatItem> itemExistente = materiaisUtilizados.stream()
                    .filter(item -> item.material().getId() == material.getId())
                    .findFirst();

            if (itemExistente.isPresent()) {
                MatItem item = itemExistente.get();
                materiaisUtilizados.remove(item);
                materiaisUtilizados.add(new MatItem(material, item.quant() + 1));
            } else {
                materiaisUtilizados.add(new MatItem(material, 1));
            }
            obsMateriais.setAll(materiaisUtilizados);
            cbMaterial.getSelectionModel().clearSelection();
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um material para adicionar.").showAndWait();
        }
    }

    @FXML
    public void onRemoveMaterial(ActionEvent actionEvent) {
        MatItem item = tvMateriais.getSelectionModel().getSelectedItem();

        if (item != null) {
            materiaisUtilizados.remove(item);
            obsMateriais.setAll(materiaisUtilizados);
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um material para remover.").showAndWait();
        }
    }

    @FXML
    public void onAddProcedimento(ActionEvent actionEvent) {
        Procedimento procedimento = cbProcedimento.getSelectionModel().getSelectedItem();

        if (procedimento != null) {
            boolean jaExiste = obsProcedimentos.stream()
                    .anyMatch(p -> p.getId() == procedimento.getId());

            if (!jaExiste) {
                obsProcedimentos.add(procedimento);
                cbProcedimento.getSelectionModel().clearSelection();
            } else {
                new Alert(Alert.AlertType.WARNING, "Este procedimento já foi adicionado.").showAndWait();
                cbProcedimento.getSelectionModel().clearSelection();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um procedimento para adicionar.").showAndWait();
        }
    }

    @FXML
    public void onRemoveProcedimento(ActionEvent actionEvent) {
        Procedimento procedimento = tvProcedimentos.getSelectionModel().getSelectedItem();

        if (procedimento != null) {
            obsProcedimentos.remove(procedimento);
        } else {
            new Alert(Alert.AlertType.WARNING, "Selecione um procedimento para remover.").showAndWait();
        }
    }

    public void onTopicosDentista(ActionEvent actionEvent) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(DentalFX.class.getResource("help-dentista-view.fxml"));
        Stage stage=new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Ajuda DentalFX Dentista");
        stage.setScene(scene);
        stage.showAndWait();
    }
}
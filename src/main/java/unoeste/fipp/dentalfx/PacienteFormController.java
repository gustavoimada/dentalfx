package unoeste.fipp.dentalfx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.json.JSONObject;
import unoeste.fipp.dentalfx.db.dals.PessoaDAL;
import unoeste.fipp.dentalfx.db.entidades.Paciente;
import unoeste.fipp.dentalfx.utils.MaskFieldUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

public class PacienteFormController implements Initializable {

    @FXML private TextField tfBairro;
    @FXML private TextField tfCEP;
    @FXML private TextField tfCPF;
    @FXML private TextField tfCidade;
    @FXML private TextField tfEmail;
    @FXML private TextField tfHistorico;
    @FXML private TextField tfID;
    @FXML private TextField tfNome;
    @FXML private TextField tfNumero;
    @FXML private TextField tfRua;
    @FXML private TextField tfTelefone;
    @FXML private TextField tfUF;

    private Paciente pacienteEmEdicao;

    @FXML
    void onBuscarCep(KeyEvent event) {
        if (tfCEP.getText().length()==9) {
            String json=consultaCep(tfCEP.getText(),"json");
            JSONObject jsonObject=new JSONObject(json);
            tfRua.setText(jsonObject.getString("logradouro"));
            tfCidade.setText(jsonObject.getString("localidade"));
            tfBairro.setText(jsonObject.getString("bairro"));
            tfUF.setText(jsonObject.getString("uf"));
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        tfRua.getScene().getWindow().hide();
        PacienteTableController.paciente = null;
    }

    @FXML
    void onConfirmar(ActionEvent event) {
        PessoaDAL dal = new PessoaDAL();

        Paciente paciente = (this.pacienteEmEdicao != null) ? this.pacienteEmEdicao : new Paciente();

        paciente.setNome(tfNome.getText());
        paciente.setCpf(tfCPF.getText());
        paciente.setCep(tfCEP.getText());
        paciente.setCidade(tfCidade.getText());
        paciente.setBairro(tfBairro.getText());
        paciente.setNumero(tfNumero.getText());
        paciente.setRua(tfRua.getText());
        paciente.setUf(tfUF.getText());
        paciente.setEmail(tfEmail.getText());
        paciente.setTelefone(tfTelefone.getText());
        paciente.setHistorico(tfHistorico.getText());

        boolean sucesso = false;

        if (paciente.getId() > 0){
            // eh uma alteração
            sucesso = dal.alterar(paciente);
            if (!sucesso) {
                new Alert(Alert.AlertType.ERROR, "Erro ao alterar o Paciente.").showAndWait();
                return;
            }
        }
        else {
            // eh um novo paciente
            sucesso = dal.gravar(paciente);
            if (!sucesso) {
                new Alert(Alert.AlertType.ERROR, "Erro ao gravar o novo Paciente.").showAndWait();
                return;
            }
        }

        tfNome.getScene().getWindow().hide();
        PacienteTableController.paciente = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MaskFieldUtil.cpfField(tfCPF);
        MaskFieldUtil.cepField(tfCEP);
        MaskFieldUtil.foneField(tfTelefone);
        tfID.setDisable(true);

        Platform.runLater(()->{
            tfNome.requestFocus();

            if(PacienteTableController.paciente instanceof Paciente pacienteParaAlterar)
            {
                this.pacienteEmEdicao = pacienteParaAlterar;

                tfID.setText(""+pacienteParaAlterar.getId());
                tfNome.setText(pacienteParaAlterar.getNome());
                tfCPF.setText(pacienteParaAlterar.getCpf());
                tfCEP.setText(pacienteParaAlterar.getCep());
                tfCidade.setText(pacienteParaAlterar.getCidade());
                tfBairro.setText(pacienteParaAlterar.getBairro());
                tfNumero.setText(pacienteParaAlterar.getNumero());
                tfRua.setText(pacienteParaAlterar.getRua());
                tfUF.setText(pacienteParaAlterar.getUf());
                tfEmail.setText(pacienteParaAlterar.getEmail());
                tfTelefone.setText(pacienteParaAlterar.getTelefone());
                tfHistorico.setText(pacienteParaAlterar.getHistorico());
                PacienteTableController.paciente = null;
            }
        });
    }

    public String consultaCep(String cep, String formato)
    {
        StringBuffer dados = new StringBuffer();
        try {
            URL url = new URL("https://viacep.com.br/ws/"+ cep + "/"+formato+"/");
            URLConnection con = url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setAllowUserInteraction(false);
            InputStream in = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s = "";
            while (null != (s = br.readLine()))
                dados.append(s);
            br.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dados.toString();
    }
}
package unoeste.fipp.dentalfx;

import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpAdmController implements Initializable{
    public WebView webview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        WebEngine webEngine = webview.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(new File("ajuda/main.html").toURI().toString());
    }
}

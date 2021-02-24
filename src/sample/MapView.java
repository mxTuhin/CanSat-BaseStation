package sample;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class MapView implements Initializable {

    @FXML
    private WebView webView;
    private WebEngine engine;


    @FXML
    private JFXButton showMap;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine=webView.getEngine();
    }


    public void openMap(javafx.event.ActionEvent actionEvent) {
        engine.load("https://google.com/maps");
    }
}

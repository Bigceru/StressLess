package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class UserPage{
    @FXML
    private AnchorPane leftAnchor;
    private MainApplication newScene = new MainApplication();
    @FXML
    private StackPane stackPane;
    public static Stage stage;

    public void start(Stage stage) throws Exception {
        this.stage = stage; // initialize value of stage.
        Parent root = FXMLLoader.load(getClass().getResource("UserPage.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public void addScene(String pathFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFXML));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}

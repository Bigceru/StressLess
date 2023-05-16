package it.univr.telemedicina.controller;

<<<<<<< HEAD
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
=======
import it.univr.telemedicina.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
>>>>>>> e54b129 (Upload 1.2 - Better GUI)
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
<<<<<<< HEAD
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class UserPage {
    @FXML
    private AnchorPane leftAnchor;

    public void inizialize(ActionEvent event){


    }

=======
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

>>>>>>> e54b129 (Upload 1.2 - Better GUI)
}

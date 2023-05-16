package it.univr.telemedicina;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Identificazione utente");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void changeScene(String source,String title, ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(source));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
<<<<<<< HEAD
        stage.setResizable(false);
=======
        stage.setResizable(true);
>>>>>>> e54b129 (Upload 1.2 - Better GUI)
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

    //Metodo per il pop-up di errore
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
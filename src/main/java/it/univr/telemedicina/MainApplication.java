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
    /**
     *  Load the first page (welcome page)
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Identificazione utente");
        stage.setScene(scene);

        // TODO: non va un cazzo la dimensione del login quando faccio il LogOut
        stage.minHeightProperty().set(600);
        stage.minWidthProperty().set(700);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /***
     * Allows to change the login scene
     * @param source location of FXML
     * @param title title of page
     * @param actionEvent the click event
     * @throws IOException
     */

    public void changeScene(String source,String title, ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(source));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        System.out.println("Source: " + source + "Title: " + title);
        if(source.equals("Login.fxml")) {
            System.out.println("CIAO");
            stage.minHeightProperty().set(600);
            stage.minWidthProperty().set(700);
            stage.setMaxHeight(600);
            stage.setMaxWidth(700);
            stage.setResizable(false);
        }
        else if(source.equals("Registration.fxml")){
            stage.minHeightProperty().set(700);
            stage.minWidthProperty().set(900);
            stage.setMaxHeight(700);
            stage.setMaxWidth(900);
            stage.setResizable(false);
        }
        else {
            stage.minHeightProperty().set(720);
            stage.minWidthProperty().set(1080);
            stage.setMaxHeight(2160);
            stage.setMaxWidth(3840);
            stage.setResizable(true);
        }

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /***
     * Close the current scene and load a new scene
     * @param pathFXML location of FXML
     * @param actionEvent the click event
     * @throws IOException
     */
    public void closeScene(String pathFXML, ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathFXML));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    /***
     * Add a new scene
     * @param pathFXML location of FXML
     * @throws IOException
     */
    public void addScene(String pathFXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFXML));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /***
     * Pop-up indicating if the generated event was successful or not
     * @param title title of the error
     * @param message indicates the error
     * @param alertType indicates the type of error
     */
    public void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
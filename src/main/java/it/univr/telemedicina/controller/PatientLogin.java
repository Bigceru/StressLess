package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.utilities.DatabaseManager;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class PatientLogin {
    private MainApplication newScene = new MainApplication();
    private DatabaseManager db = new DatabaseManager();

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    private ArrayList<String> credentials = new ArrayList<>();

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Change page if login has success
        if(username.isEmpty() || password.isEmpty() ){
            newScene.showAlert("Campi non pieni","Compila tutti i campi");
        }
        else {
            try {

                // connect to the database
                Database database = new Database(2);
                credentials = database.getQuery("SELECT * FROM Patients WHERE username = " + "\"" + txtUsername.getText() + "\" AND password = " + "\"" + txtPassword.getText() + "\"", new String[]{"username", "password"});

                // if is empty run error
                if (credentials.isEmpty())
                    newScene.showAlert("Error with credentials","Error with credentials");

                database.closeAll();

                // change scene
                newScene.changeScene("UserPage.fxml", "Profilo Utente", actionEvent);

            } catch (SQLException e) {
                System.out.println("Error with login");

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleRegisterButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("Registration.fxml", "Registrazione utente", actionEvent);
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("Login.fxml", "Identificazione utente", actionEvent);
    }
}

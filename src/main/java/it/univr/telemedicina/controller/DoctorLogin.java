package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DoctorLogin {

    MainApplication newScene = new MainApplication();
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;

    private ArrayList<String> credentials = new ArrayList<>();

    public void handleLogin(ActionEvent actionEvent){
        String username = txtUsername.getText();
        String password = txtPassword.getText();


        if(username.isEmpty() || password.isEmpty() ){
            newScene.showAlert("Campi non pieni","Compila tutti i campi");
        }
        else {
            try {
                Database database = new Database(2);
                credentials = database.getQuery("SELECT * FROM Doctors WHERE username = " + "\"" + txtUsername.getText() + "\" AND password = " + "\"" + txtPassword.getText() + "\"", new String[]{"username", "password"});
                if (credentials.isEmpty())
                    newScene.showAlert("Error with credentials","Error with credentials");
                database.closeAll();
            } catch (SQLException e) {
                System.out.println("Error with login");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void handleBack (ActionEvent actionEvent) throws IOException {
        newScene.changeScene("Login.fxml","login",actionEvent);

    }
}


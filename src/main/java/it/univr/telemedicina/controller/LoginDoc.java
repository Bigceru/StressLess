package it.univr.telemedicina.controller;

import it.univr.telemedicina.HelloApplication;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginDoc {

    HelloApplication newScene = new HelloApplication();
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;

    private ArrayList<String> credenziali = new ArrayList<>();

    public void handleLogin(ActionEvent actionEvent){
        String userName = txtUsername.getText();
        String password = txtPassword.getText();
        if(userName.isEmpty() || password.isEmpty() ){
            newScene.showAlert("Campi non pieni","Compila tutti i campi");
        }
        else {
            try {
                Database database = new Database(2);
                credenziali = database.getQuery("SELECT * FROM Doctors WHERE Username = " + "\"" + txtUsername.getText() + "\" AND Password = " + "\"" + txtPassword.getText() + "\"", new String[]{"Username", "Password"});
                if (credenziali.isEmpty())
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
        newScene.changeScene("login.fxml","login",actionEvent);

    }
}


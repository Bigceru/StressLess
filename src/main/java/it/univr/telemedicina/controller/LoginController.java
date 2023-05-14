package it.univr.telemedicina.controller;

import it.univr.telemedicina.HelloApplication;
import javafx.event.ActionEvent;

import java.io.IOException;

public class LoginController {
    private final HelloApplication newScene = new HelloApplication();

    public void loginMedicoButton(ActionEvent actionEvent) throws IOException{
        newScene.changeScene("loginDoc.fxml", "Doctor", actionEvent);
    }

    public void loginPazienteButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("loginPaziente.fxml", "Paziente", actionEvent);
    }
}
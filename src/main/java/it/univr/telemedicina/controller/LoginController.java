package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private final MainApplication newScene = new MainApplication();
    public void loginDoctorButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("DoctorLogin.fxml", "Doctor", actionEvent);
    }

    public void loginPatientButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("PatientLogin.fxml", "Paziente", actionEvent);
    }


}
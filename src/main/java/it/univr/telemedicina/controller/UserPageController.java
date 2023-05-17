package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Patient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class UserPageController implements Initializable{

    private MainApplication newScene = new MainApplication();
    private static Patient patient;
    @FXML
    private Label lblUsername;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblUsername.setText(patient.getName());
    }

    public void setPatient(Patient patient){
        UserPageController.patient = patient;
    }
}

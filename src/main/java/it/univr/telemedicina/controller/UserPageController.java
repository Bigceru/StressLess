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
    private Label lblName;
    @FXML
    private Label lblRefDoc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblName.setText(patient.getName());

        // Set doctor's name/surname label
        try {
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM Doctors WHERE ID = " + patient.getRefDoc(), new String[]{"Name","Surname"});    // Query to get doctor name
            lblRefDoc.setText("Dr. " + info.get(0) + " " + info.get(1));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPatient(Patient patient){
        UserPageController.patient = patient;
    }
}

package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Doctor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorPageController implements Initializable {
    private final MainApplication newScene = new MainApplication();
    private static Doctor doctor;
    @FXML
    public AnchorPane DoctorHomeScene;
    @FXML
    public AnchorPane DoctorStatisticsScene;
    @FXML
    public Button buttonLogOut;
    @FXML
    public Button buttonEditProfile;
    @FXML
    public Button buttonSearchPatient;
    @FXML
    public Button buttonHome;
    @FXML
    public Button buttonStatistic;
    @FXML
    public Label lblDoctorName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        DoctorHomeScene.setVisible(true);
        DoctorStatisticsScene.setVisible(false);
        lblDoctorName.setText("Dott. " + doctor.getName().charAt(0) + ". " + doctor.getSurname());
    }

    public void handleChangeScene(ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource() == buttonHome) {  // Home button click
            DoctorHomeScene.setVisible(true);
            DoctorStatisticsScene.setVisible(false);

            // Edit button style to show the clicked one
            DoctorHomeScene.setStyle("-fx-background-color: #2A7878");
            DoctorStatisticsScene.setStyle("-fx-background-color: #0000");
        } else if (actionEvent.getSource() == buttonStatistic) {    // Statistic button click
            DoctorHomeScene.setVisible(false);
            DoctorStatisticsScene.setVisible(true);

            // Edit button style to show the clicked one
            DoctorHomeScene.setStyle("-fx-background-color: #0000");
            DoctorStatisticsScene.setStyle("-fx-background-color: #2A7878");
        }
        else if(actionEvent.getSource() == buttonLogOut) {     // LogOut button click
            newScene.start((Stage) ((Node) actionEvent.getSource()).getScene().getWindow());
        }
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}

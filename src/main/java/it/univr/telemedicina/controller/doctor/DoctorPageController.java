package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DoctorPageController implements Initializable {
    private final MainApplication newScene = new MainApplication();
    public AnchorPane DoctorHomeScene;
    public AnchorPane DoctorStatisticsScene;
    public Button buttonLogOut;
    public Button buttonEditProfile;
    public Button buttonSearchPatient;
    public Button buttonHome;
    public Button buttonStatistic;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DoctorHomeScene.setVisible(true);
        DoctorStatisticsScene.setVisible(false);
    }
}

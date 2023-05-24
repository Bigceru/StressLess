package it.univr.telemedicina.controller.doctor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;

import java.util.Date;

public class DoctorStatisticsSceneController {

    @FXML
    private BarChart<?,?> barChart;
    @FXML
    private RadioButton radioP1Low;
    @FXML
    private RadioButton radioPOptimal;
    @FXML
    private RadioButton radioPNormal;
    @FXML
    private RadioButton radioPNormalHigh;
    @FXML
    private RadioButton radioP1Borderline;
    @FXML
    private RadioButton radioP2Moderate;
    @FXML
    private RadioButton radioP3Critic;
    @FXML
    private RadioButton radioPISBorderline;
    @FXML
    private RadioButton radioPIS;
    @FXML
    private RadioButton radioTDuretic;
    @FXML
    private RadioButton radioTCalcium;
    @FXML
    private RadioButton radioTSympatholytic;
    @FXML
    private RadioButton radioTBeta;
    @FXML
    private RadioButton radioTACE;
    @FXML
    private RadioButton radioTSart;
    @FXML
    private Button buttonShowGraph;
    @FXML
    private Label lblActualGraphic;
    @FXML
    private DatePicker dateStart;
    @FXML
    private DatePicker dateEnd;
    @FXML
    private Tab tabPressure;
    @FXML
    private Tab tabTherapies;

    public void changeTab(){
        System.out.print(tabPressure.getOnSelectionChanged().toString());
    }
    public void showNewGraph(ActionEvent actionEvent) {
    }

}

package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Condition;

import javafx.scene.chart.XYChart.Data;

public class DoctorStatisticsSceneController {

    @FXML
    private StackedBarChart<String, Integer> stackedBarChart;
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

    // Doctor instance
    private static Doctor doctor;

    public void changeTab(){
        System.out.print(tabPressure.getOnSelectionChanged().toString());
    }

    public void showNewGraph(ActionEvent actionEvent) {


        createGraph();
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void createGraph() {
        ArrayList<String> list;
        LocalDate start = dateStart.getValue();
        LocalDate end = dateEnd.getValue();

        //Check date
        if (start.isAfter(end)) {
            dateStart.setStyle("-fx-text-fill: red;");
            dateEnd.setStyle("-fx-text-fill: red;");
            return;
        } else {
            dateStart.setStyle("-fx-text-fill: black;");
            dateEnd.setStyle("-fx-text-fill: black;");
        }

        try {
            Database db = new Database(2);
            list = db.getQuery("SELECT Date, ConditionPressure FROM BloodPressures WHERE Date BETWEEN '" + start.toString() + "' AND '" + end.toString() + "'", new String[]{"Date", "ConditionPressure"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ArrayList<LocalDate> allDate = new ArrayList<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(allDate::add);
        allDate.add(end);

        XYChart.Series<String, Integer>[] series = new XYChart.Series[9];
        int i = 0;

        /*
        for(i=0; i<9; i++){
            series[i] = new XYChart.Series<>();
            if(i == 1 )
                series[i].setName("Normale");
            else{
                series[i].setName(i+"");
            }

         */

        ArrayList<String> category = new ArrayList<>(Arrays.asList("Ottimale","Normale","Normale - alta","Ipertensione di Grado 1 borderline","Ipertensione di Grado 1 lieve","Ipertensione di Grado 2 moderata", "Ipertensione di Grado 3 grave", "Ipertensione sistolica isolata borderline", "Ipertensione sistolica isolata"));
            for(String s: category) {
                series[i] = new XYChart.Series<>();
                series[i].setName(s);
                for (LocalDate date : allDate) {
                    series[i].getData().add(returnNumberCondition(date.toString(), list, s));
                    /*
                    if (radioPOptimal.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ottimale"));
                    if (radioPNormal.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Normale"));
                    if (radioPNormalHigh.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Normale - alta"));
                    if (radioP1Borderline.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione di Grado 1 borderline"));
                    if (radioP1Low.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione di Grado 1 lieve"));
                    if (radioP2Moderate.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione di Grado 2 Moderata"));
                    if (radioP3Critic.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione di Grado 3 grave"));
                    if (radioPISBorderline.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione sistolica isolata borderline"));
                    if (radioPIS.isSelected())
                        series[i].getData().add(returnNumberCondition(date.toString(), list, "Ipertensione sistolica isolata"));
                    //Add number on
                /*
                series[i].getData().forEach(data -> {
                    Label label = new Label(data.getYValue().toString());
                    label.setAlignment(Pos.TOP_CENTER);
                    label.setStyle("-fx-font-size: 16px; -fx-text-fill: white");
                    data.setNode(label);
                    data.getNode().setNodeOrientation(NodeOrientation.INHERIT);
                });*/
                }
                i++;
            }
        stackedBarChart.getRotationAxis();
        stackedBarChart.getData().addAll(series[1]);


        stackedBarChart.setAnimated(false);
        }




    private XYChart.Data<String, Integer> returnNumberCondition(String date, ArrayList<String> list, String condition) {
        int count = 0;
        for (int i = 0; i < list.size() - 1; i = i + 2) {
            // Date equal i have value
            if (list.get(i).equals(date) && list.get(i + 1).equals(condition)) {
                count++;
            }
        }
        return new XYChart.Data<>(date, count);
    }


}

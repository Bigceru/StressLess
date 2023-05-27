package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

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
    private int pos = 1;    // Position of week/month

    public void changeTab() {
        stackedBarChart.getData().clear();
        if (tabPressure.isSelected()) {
            lblActualGraphic.setText("ANDAMENTO PRESSIONE");
            // remove all selected radio button in the other tab
            if (radioTACE != null)
                radioTACE.setSelected(false);
            if (radioTBeta != null)
                radioTBeta.setSelected(false);
            if (radioTCalcium != null)
                radioTCalcium.setSelected(false);
            if (radioTDuretic != null)
                radioTDuretic.setSelected(false);
            if (radioTSart != null)
                radioTSart.setSelected(false);
            if (radioTSympatholytic != null)
                radioTSympatholytic.setSelected(false);
        } else {
            lblActualGraphic.setText("ANDAMENTO TERAPIE");
            if (radioP1Borderline != null)
                radioP1Borderline.setSelected(false);
            if (radioPIS != null)
                radioPIS.setSelected(false);
            if (radioP1Low != null)
                radioP1Low.setSelected(false);
            if (radioP2Moderate != null)
                radioP2Moderate.setSelected(false);
            if (radioPISBorderline != null)
                radioPISBorderline.setSelected(false);
            if (radioP3Critic != null)
                radioP3Critic.setSelected(false);
            if (radioPNormal != null)
                radioPNormal.setSelected(false);
            if (radioPNormalHigh != null)
                radioPNormalHigh.setSelected(false);
            if (radioPOptimal != null)
                radioPOptimal.setSelected(false);
        }
    }

    public void showNewGraph(ActionEvent actionEvent) {
        if(dateStart.getValue() == null || dateEnd.getValue() == null)
            return;
        if(tabPressure.isSelected())
            createGraphPression();
        else
            createGraphTherapie();
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void createGraphPression() {
        // Reset data
        stackedBarChart.getData().clear();
        ArrayList<String> list;
        LocalDate start = dateStart.getValue();
        LocalDate end = dateEnd.getValue();

        // Check date
        if (start.isAfter(end) || end.equals(null) || start.equals(null)) {
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

        // All category
        ArrayList<String> categorySelected = new ArrayList<>();

        // Inizialite categorySelected
        if (radioPOptimal.isSelected())
            categorySelected.add("Ottimale");
        if (radioPNormal.isSelected())
            categorySelected.add("Normale");
        if (radioPNormalHigh.isSelected())
            categorySelected.add("Normale - alta");
        if (radioP1Borderline.isSelected())
            categorySelected.add("Ipertensione di Grado 1 borderline");
        if (radioP1Low.isSelected())
            categorySelected.add("Ipertensione di Grado 1 lieve");
        if (radioP2Moderate.isSelected())
            categorySelected.add("Ipertensione di Grado 2 moderata");
        if (radioP3Critic.isSelected())
            categorySelected.add("Ipertensione di Grado 3 grave");
        if (radioPISBorderline.isSelected())
            categorySelected.add("Ipertensione sistolica isolata borderline");
        if (radioPIS.isSelected())
            categorySelected.add("Ipertensione sistolica isolata");

        // Call method to set Graph
        setGraph(categorySelected, start, end, allDate, list);
    }

    public void createGraphTherapie(){
        // reset data
        stackedBarChart.getData().clear();
        ArrayList<String> queryResult;
        LocalDate start = dateStart.getValue();
        LocalDate end = dateEnd.getValue();

        //Check date
        if (end.equals(null) || start.equals(null) || start.isAfter(end)) {
            dateStart.setStyle("-fx-text-fill: red;");
            dateEnd.setStyle("-fx-text-fill: red;");
            return;
        } else {
            dateStart.setStyle("-fx-text-fill: black;");
            dateEnd.setStyle("-fx-text-fill: black;");
        }

        try {
            Database db = new Database(2);
            queryResult = db.getQuery("SELECT StartDate, TherapyName FROM Therapies WHERE StartDate >='" + start.toString() + "'", new String[]{"StartDate", "TherapyName"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ArrayList<LocalDate> allDate = new ArrayList<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(allDate::add);
        allDate.add(end);

        //All category
        ArrayList<String> therapiesSelected = new ArrayList<>();

        if (radioTACE.isSelected())
            therapiesSelected.add("ACE-inibitori");
        if (radioTBeta.isSelected())
            therapiesSelected.add("Beta bloccanti");
        if (radioTCalcium.isSelected())
            therapiesSelected.add("Calcio-antagonisti");
        if (radioTDuretic.isSelected())
            therapiesSelected.add("Diuretiche");
        if (radioTSart.isSelected())
            therapiesSelected.add("Sartani");
        if (radioTSympatholytic.isSelected())
            therapiesSelected.add("Simpaticolitici");

        setGraph(therapiesSelected, start, end, allDate, queryResult);
    }

    private void setGraph(ArrayList<String> categorySelected, LocalDate start, LocalDate end, ArrayList<LocalDate> allDate, ArrayList<String> queryResult){
        int j = 0;  // Go trough days
        XYChart.Series<String, Integer>[] series = new XYChart.Series[categorySelected.size()];

        // Cycle all the categories selected
        for (String category : categorySelected) {
            int i = categorySelected.indexOf(category);     // Go trough categories
            series[i] = new XYChart.Series<>();     // Create a new series for the category
            series[i].setName(category);
            pos = 1;

            // 14 days or less
            if (ChronoUnit.DAYS.between(start, end) <= 14) {
                for (LocalDate date : allDate)
                    series[i].getData().add(returnNumberCondition(date.toString(), null, queryResult, category));
            }
            // 31 days
            else if (ChronoUnit.DAYS.between(start, end) <= 31)  {
                // I take the range of one week
                for (j = 0; j < allDate.size() - 6; j += 7) {
                    series[i].getData().add(returnNumberCondition(allDate.get(j).toString(), allDate.get(j + 6).toString(), queryResult, category));
                    pos++;
                }
                // I check if there are other days less than 7 days
                if(allDate.size()-1-j != 0 ){
                    series[i].getData().add(returnNumberCondition(allDate.get(j).toString(), allDate.get(allDate.size()-1).toString(), queryResult, category));
                    pos++;
                }
            }
            else {
                // I take the range of one month
                for (j = 0; j < allDate.size() - 29; j += 30) {
                    series[i].getData().add(returnNumberCondition(allDate.get(j).toString(), allDate.get(j + 29).toString(), queryResult, category));
                    pos++;
                }

                // I check if there are other days less than 30 days
                if(allDate.size()-1-j != 0 ){
                    series[i].getData().add(returnNumberCondition(allDate.get(j).toString(), allDate.get(allDate.size()-1).toString(), queryResult, category));
                    pos++;
                }
            }

            // Set number on bar
            series[i].getData().forEach(data -> {
                Label label = new Label(data.getYValue().toString());
                label.setAlignment(Pos.TOP_CENTER);
                label.setStyle("-fx-font-size: 16px; -fx-text-fill: white");
                data.setNode(label);
                data.getNode().setNodeOrientation(NodeOrientation.INHERIT);

            });
        }

        //stackedBarChart.setStyle(".chart-series-0 .default-color8.chart-bar { -fx-bar-fill: black;} ");
        stackedBarChart.getData().addAll(series);
        stackedBarChart.setAnimated(false);
    }

    /**
     * Method that calculates occurrences in the specified period and which satisfies a certain condition
     * @param dateStart
     * @param dateEnd
     * @param queryResult
     * @param condition pressure or therapy
     * @return XYChart.Data<String, Integer>    return XYChart.Data to add to the Series, String is value for day range and Integer is number of occurrences of the condition in the date range
     */
    private XYChart.Data<String, Integer> returnNumberCondition(String dateStart,String dateEnd, ArrayList<String> queryResult, String condition) {
        int count = 0;

        // If the range of time is <= 14 days
        if(dateEnd == null) {
            // Cycle all the therapies/pressures and find math with startDate and condition (therapy/pressure)
            for (int i = 0; i < queryResult.size() - 1; i = i + 2) {
                // If I found equal date values and equal conditions
                if (queryResult.get(i).equals(dateStart) && queryResult.get(i + 1).equals(condition)) {
                    count++;
                }
            }
            return new XYChart.Data<>(dateStart, count);
        }
        else {  // If the range of time is > 14 days, Part with defined end
            // Reconvert the String date to LocalDate type
            LocalDate start = LocalDate.parse(dateStart);
            LocalDate end = LocalDate.parse(dateEnd);

            // Check if the condition is validated between the start and end dates
            for (int i = 0; i < queryResult.size() - 1; i = i + 2) {
                // If I found equal date values and equal conditions
                if (((LocalDate.parse(queryResult.get(i)).isAfter(start) && LocalDate.parse(queryResult.get(i)).isBefore(end)) || ((LocalDate.parse(queryResult.get(i)).isEqual(start) || LocalDate.parse(queryResult.get(i)).isEqual(end)))) && queryResult.get(i + 1).equals(condition)) {
                    count++;
                }
            }

            // Set String value for day range
            String fase; // Variable indicating the period
            if(ChronoUnit.DAYS.between(start, end) <= 7)
                fase = "Settimana " + pos;
            else
                fase = "Mese " + pos;
            return new XYChart.Data<>(fase, count);
        }
    }
}
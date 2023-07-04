package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.models.Pressure;
import it.univr.telemedicina.models.PressureList;
import it.univr.telemedicina.models.Therapy;
import it.univr.telemedicina.models.TherapyList;
import it.univr.telemedicina.models.users.Doctor;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.PressureField;
import it.univr.telemedicina.utilities.TherapyFields;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class DoctorStatisticsSceneController {
    MainApplication newScene = new MainApplication();
    @FXML
    public Button buttonShowGraph;
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
    private DatePicker dateStart;
    @FXML
    private DatePicker dateEnd;
    @FXML
    private Tab tabPressure;

    // All category selected
    ArrayList<String> categorySelected = new ArrayList<>();

    // ArrayList to store the pressure to use for Analyze table
    public static ArrayList<Pressure> nicePressureForTable = new ArrayList<>();
    public static ArrayList<Therapy> niceTherapyForTable = new ArrayList<>();

    // Doctor instance
    private static Doctor doctor;
    private int pos = 1;    // Position of week/month

    /***
     * Change the selected tab and update the bar graph.
     * If the "Pressure" tab is selected, the graph will show the pressure trend.
     * If the "Therapies" tab is selected, the graph will show the progress of the therapies.
     */
    public void changeTab() {
        // Clear variable and graph
        stackedBarChart.getData().clear();
        nicePressureForTable.clear();
        niceTherapyForTable.clear();

        if (tabPressure.isSelected()) {
            stackedBarChart.setTitle("ANDAMENTO PRESSIONI");
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
            stackedBarChart.setTitle("ANDAMENTO TERAPIE");
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

    /***
     * shows the graph according to the tab (Pression or Therapie)
     * @param actionEvent
     */
    public void showNewGraph(ActionEvent actionEvent) {
        if(dateStart.getValue() == null || dateEnd.getValue() == null)
            return;
        if(tabPressure.isSelected())
            createGraphPression();
        else
            createGraphTherapie();
    }

    /***
     * Setting doctor for the DoctorStatisticsSceneController
     * @param doctor the doctor to set
     */
    public void setDoctor(Doctor doctor) {
        DoctorStatisticsSceneController.doctor = doctor;
    }

    /***
     * creation of the bar graph based on the selected categories of pressures
     */
    public void createGraphPression() {
        // Reset data
        stackedBarChart.getData().clear();
        nicePressureForTable.clear();

        ArrayList<String> list = new ArrayList<>();
        LocalDate start = dateStart.getValue();
        LocalDate end = dateEnd.getValue();

        // Check date
        if (end == null || start == null || start.isAfter(end)) {
            dateStart.setStyle("-fx-text-fill: red;");
            dateEnd.setStyle("-fx-text-fill: red;");
            return;
        } else {
            dateStart.setStyle("-fx-text-fill: black;");
            dateEnd.setStyle("-fx-text-fill: black;");
        }

        ArrayList<Pressure> pressureListForAnalyzeTable;
        try {
            Database db = new Database(2);

            PressureList pressureList = new PressureList();
            pressureListForAnalyzeTable = new ArrayList<>();

            // Cycle for every Patient I find
            for (String s : getPatientsConditionsQuery()) {
                list.addAll(pressureList.getWhatUWantString(pressureList.getPressureToString(pressureList.getPressuresByDate(Integer.parseInt(s), start, end)), new PressureField[]{PressureField.DATE, PressureField.CONDITION_PRESSURE}));
                pressureListForAnalyzeTable.addAll(pressureList.getPressuresByDate(Integer.parseInt(s), start, end));
            }

            // Query the database to get pressure data for the selected period
            // list = db.getQuery("SELECT Date, ConditionPressure FROM BloodPressures WHERE Date BETWEEN '" + start + "' AND '" + end + "' AND (" + getPatientsConditionsQuery() + ")", new String[]{"Date", "ConditionPressure"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<LocalDate> allDate = new ArrayList<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(allDate::add);
        allDate.add(end);

        // Inizialite categorySelected
        categorySelected.clear();   // Clear category selected array to add the new one
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

        // Method to get only the pressure selected
        setPressureFromCategory(categorySelected, pressureListForAnalyzeTable);
        // Call method to set Graph
        setGraph(categorySelected, start, end, allDate, list);
    }

    /**
     * Method to fill the nicePressureForTable ArrayList (contains pressures to use in the Analyze table)
     * @param categorySelected condition which the pressure must have
     * @param listOfPressure list of all pressure to filter by condition
     */
    private void setPressureFromCategory(ArrayList<String> categorySelected, ArrayList<Pressure> listOfPressure) {
        // Cycle all the pressure and take only the ones with the specified condition
        for(Pressure pressure : listOfPressure) {
            if(categorySelected.contains(pressure.getConditionPressure()))
                nicePressureForTable.add(pressure);
        }
    }

    /***
     * creation of the bar graph based on the selected categories of therapies
     */
    public void createGraphTherapie(){
        // reset data
        stackedBarChart.getData().clear();
        niceTherapyForTable.clear();

        LocalDate start = dateStart.getValue();
        LocalDate end = dateEnd.getValue();
        ArrayList<String> queryResult = new ArrayList<>();

        //Check date
        if (end == null || start == null || start.isAfter(end)) {
            dateStart.setStyle("-fx-text-fill: red;");
            dateEnd.setStyle("-fx-text-fill: red;");
            return;
        } else {
            dateStart.setStyle("-fx-text-fill: black;");
            dateEnd.setStyle("-fx-text-fill: black;");
        }

        TherapyList therapyList = new TherapyList();
        ArrayList<Therapy> therapyListForAnalyzeTable = new ArrayList<>();

        // Cycle for every Patient I find
        for(String id : getPatientsConditionsQuery()){
            queryResult.addAll(therapyList.getWhatUWantString(therapyList.getTherapyToString(therapyList.getTherapyByDate(Integer.parseInt(id), start, null)), new TherapyFields[]{TherapyFields.START_DATE,TherapyFields.THERAPY_NAME}));
            therapyListForAnalyzeTable.addAll(therapyList.getTherapyByDate(Integer.parseInt(id), start, null));
        }

        // Query the database to get therapy data for the selected period
        ArrayList<LocalDate> allDate = new ArrayList<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(allDate::add);
        allDate.add(end);

        // All category
        ArrayList<String> therapiesSelected = new ArrayList<>();

        if (radioTACE.isSelected())
            therapiesSelected.add("ACE INIBITORI");
        if (radioTBeta.isSelected())
            therapiesSelected.add("BETA BLOCCANTI");
        if (radioTCalcium.isSelected())
            therapiesSelected.add("CALCIO ANTAGONISTI");
        if (radioTDuretic.isSelected())
            therapiesSelected.add("DIURETICHE");
        if (radioTSart.isSelected())
            therapiesSelected.add("SARTANI");
        if (radioTSympatholytic.isSelected())
            therapiesSelected.add("SIMPATICOLITICI");

        // Method to get only the therapy selected
        setTherapyFromCategory(therapiesSelected, therapyListForAnalyzeTable, end);
        // Call method to set Graph
        setGraph(therapiesSelected, start, end, allDate, queryResult);
    }

    /**
     * Method to fill the nicePressureForTable ArrayList (contains pressures to use in the Analyze table)
     * @param therapiesSelected condition which the pressure must have
     * @param listOfTherapies list of all pressure to filter by condition
     */
    private void setTherapyFromCategory(ArrayList<String> therapiesSelected, ArrayList<Therapy> listOfTherapies, LocalDate end) {
        // Cycle all the pressure and take only the ones with the specified condition
        for(Therapy therapy : listOfTherapies) {
            if(therapiesSelected.contains(therapy.getTherapyName()) && therapy.getStartDate().compareTo(end) <= 0)
                niceTherapyForTable.add(therapy);
        }
    }

    /***
     * Set graph
     * @param categorySelected
     * @param start start date
     * @param end end date
     * @param allDate array of all date between start and end
     * @param queryResult array that contains the result of the query
     */
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
                if(allDate.size()-1-j > 0 ){
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
                if(allDate.size()-1-j > 0 ){
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
     * @param dateStart start to calculate
     * @param dateEnd final date to care about
     * @param queryResult query from DB
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

    /**
     * Method to return string to insert in the query to get only patient of the doctor
     * @return string to insert in the query
     */
    private ArrayList<String> getPatientsConditionsQuery() {
        try {
            Database database = new Database(2);

            ArrayList<String> patientsID = database.getQuery("SELECT ID FROM Patients WHERE refDoc = " + doctor.getID(), new String[]{"ID"});

            database.closeAll();

            return patientsID;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void showNewTableAnalysis() throws IOException {
        if(!(nicePressureForTable == null || nicePressureForTable.isEmpty()))
            newScene.addScene("/it/univr/telemedicina/doctorPages/DoctorTable.fxml");
        else if(!(niceTherapyForTable == null || niceTherapyForTable.isEmpty())){
            newScene.addScene("/it/univr/telemedicina/doctorPages/DoctorTable.fxml");
        }
        else
            newScene.showAlert("Errore dati", "Non sono stati definiti dei dati da analizzare!", Alert.AlertType.ERROR);
    }

    public ArrayList<String> getSelectedRadioButton() {
        return null;
    }
}
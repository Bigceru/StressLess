package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.models.PressureList;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class DoctorPlusInfoPatientController implements Initializable {
    public Line lineFigure;
    public AnchorPane infoPane;
    private int pos = 1;    // Position of week/month
    @FXML
    public LineChart<String, Long> lineChartPression;
    @FXML
    public StackedBarChart<String, Integer> barChartDrug;
    @FXML
    public TextArea txtAreaInfo;
    @FXML
    public Tab tabPressure;
    @FXML
    public Tab tabDrug;

    // Patient istance
    private static int idPatient;
    @FXML
    public DatePicker dateStart;
    @FXML
    public DatePicker dateEnd;

    /**
     * this method fill the text area where the doctor put his note about the problem of the patient
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Database database = new Database(2);
            ArrayList<String> resultInfoQuery = database.getQuery("SELECT Detail FROM PatientsDetails WHERE IDPatient = " + idPatient, new String[]{"Detail"});
            database.closeAll();

            // Add detail of patient in the textArea
            if(!resultInfoQuery.isEmpty()){
                txtAreaInfo.setText(resultInfoQuery.toString().substring(1, resultInfoQuery.toString().length()-1));
            }else{
                txtAreaInfo.setText("Non sono presenti note riguardo al paziente selezionato");
            }
        }catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method thath block eventually problem with empty field and show the graphs
     *
     */
    public void buttonSend(ActionEvent actionEvent) {
        // If startDate is before endDate or equal to see one day and both them are not null
        if(dateStart.getValue() != null && dateEnd.getValue() != null && (dateStart.getValue().isBefore(dateEnd.getValue()) || dateStart.getValue().isEqual(dateEnd.getValue()))) {
            // Set date to black color
            dateStart.setStyle("-fx-text-fill: black;");
            dateEnd.setStyle("-fx-text-fill: black;");


            // If tab pressure is pressed
            if(tabPressure.isSelected()) {
                updateGraph(dateStart.getValue(), dateEnd.getValue());

            }
            else {  // Otherwise show the barchart
                createGraphDrug(dateStart.getValue(), dateEnd.getValue());
            }
        }
        else {  // If some fields are incorrect
            // Set date to red color
            dateStart.setStyle("-fx-text-fill: red;");
            dateEnd.setStyle("-fx-text-fill: red;");
        }
    }

    /***
     * Method to manage the tabs and figure out which graph to display
     * @param event allows you to understand in which tab it was chosen
     */
    public void handleChangeTab(Event event) {
        if(event.getSource() == tabPressure) {
            lineChartPression.setVisible(true);
            barChartDrug.setVisible(false);

            // change title and clear the graph
            lineChartPression.setTitle("Grafico Pressioni");
            lineChartPression.lookup(".chart-title").setStyle("-fx-text-fill: white;");
            lineChartPression.getData().clear();
        }
        else if(event.getSource() == tabDrug){
            lineChartPression.setVisible(false);
            barChartDrug.setVisible(true);

            // change title and clear the graph
            barChartDrug.setTitle("Grafico Farmaci Assunti");
            barChartDrug.lookup(".chart-title").setStyle("-fx-text-fill: white;");
            barChartDrug.getData().clear();
        }
    }

    /***
     * Update the graph to show the systolic (red line) and diastolic(blue one) pressure
     * @param startDate from this data watch all pressures
     * @param endDate last date we care
     */
    private void updateGraph(LocalDate startDate, LocalDate endDate) {
        ArrayList<String> queryResult;

        PressureList pressureList = new PressureList();

        // QueryResult take pressure from database
        queryResult = pressureList.getPressureToString(pressureList.getPressuresByDate(idPatient,startDate,endDate));


        //lineChartPression.setTitle("Grafico Pressione");

        XYChart.Series<String, Long> series = new XYChart.Series<>();
        series.setName("Pressione Sistolica");
        XYChart.Series<String, Long> series2 = new XYChart.Series<>();
        series2.setName("Pressione Diastolica");

        // create the list with 7/30 values
        ArrayList<XYChart.Data<String, Long>> dataSeries = new ArrayList<>();
        ArrayList<XYChart.Data<String, Long>> dataSeries2 = new ArrayList<>();

        // TreeMap of (Date, [Systolic, Diastolic])
        TreeMap<LocalDate, ArrayList<Integer>> pressures = new TreeMap<>();

        for(int i = 0; i < queryResult.size() - 6; i += 7) {   // systolic, diastolic, date
            // If I already have used the pressure of this day
            if (pressures.containsKey(LocalDate.parse(queryResult.get(i + 1)))) { //ID, DATE, HOUR, SYSTOLIC, DIASTOLIC, SYMP, CONDITION
                int newSystolic = (pressures.get(LocalDate.parse(queryResult.get(i + 1))).get(0)) + Integer.parseInt(queryResult.get(i+3));
                int newDiastolic = (pressures.get(LocalDate.parse(queryResult.get(i + 1))).get(1)) + Integer.parseInt(queryResult.get(i + 4));
                pressures.put(LocalDate.parse(queryResult.get(i + 1)), new ArrayList<>(List.of(newSystolic, newDiastolic)));        // Insert the sum of the both pressures
            } else {
                pressures.put(LocalDate.parse(queryResult.get(i + 1)), new ArrayList<>(List.of(Integer.parseInt(queryResult.get(i+3)), Integer.parseInt(queryResult.get(i + 4)))));
            }
        }

        // populating the series with data
        for(LocalDate key : pressures.keySet()) {
            // For each pressure calculate the mean (take it and divide by the number of pressure taken in the same day)
            dataSeries.add(new XYChart.Data<>(key.toString(), pressures.get(key).get(0) / queryResult.stream().filter(s -> s.equals(key.toString())).count()));
            dataSeries2.add(new XYChart.Data<>(key.toString(), pressures.get(key).get(1) / queryResult.stream().filter(s -> s.equals(key.toString())).count()));
        }

        // Add the data to the series
        series.getData().addAll(dataSeries);
        series2.getData().addAll(dataSeries2);

        // Add data to graphic
        lineChartPression.getData().setAll(series, series2);

        // Set line and label colors
        series.getNode().setStyle("-fx-stroke: red;");
        series2.getNode().setStyle("-fx-stroke: blue;");
        lineChartPression.setStyle("-fx-background-color: white; CHART_COLOR_1: #ff0000; CHART_COLOR_2: #0000FF;");
        lineChartPression.setCreateSymbols(false);
        lineChartPression.setAnimated(false);
    }

    /***
     * Method for create stackedBarChart of taken drugs
     */
    public void createGraphDrug(LocalDate start, LocalDate end) {
        // Reset data
        barChartDrug.getData().clear();
        ArrayList<String> queryResult;

        try {
            Database db = new Database(2);
            //Query for taken drugs
            queryResult = db.getQuery("SELECT Date,DrugName,Quantity FROM TakenDrugs WHERE Date BETWEEN '" + start.toString() + "' AND '" + end.toString() + "'"+ "AND IDPatient = " + idPatient, new String[]{"Date", "DrugName", "Quantity"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //Array that contains all dates in the range (start-end)
        ArrayList<LocalDate> allDate = new ArrayList<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(allDate::add);
        allDate.add(end);

        // All types of drugs taken
        ArrayList<String> categorySelected = new ArrayList<>();
        // Initialization
        for(int i = 0; i < queryResult.size()-2; i = i + 3){
            // I don't take duplicates
            if(!categorySelected.contains(queryResult.get(i+1)) && queryResult.get(i+1) != null)
                categorySelected.add(queryResult.get(i+1));
        }

        int j;  // Go trough days
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

        barChartDrug.setStyle("-fx-background-color: white;");
        barChartDrug.getData().addAll(series);
        barChartDrug.setAnimated(false);

    }

    /**
     * Method that calculates occurrences in the specified period and which satisfies a certain condition
     * @param dateStart start date to see
     * @param dateEnd  last date we care
     * @param queryResult taken data from drug Database
     * @param condition pressure or therapy
     * @return XYChart.Data<String, Integer>    return XYChart.Data to add to the Series, String is value for day range and Integer is number of occurrences of the condition in the date range
     */
    private XYChart.Data<String, Integer> returnNumberCondition(String dateStart,String dateEnd, ArrayList<String> queryResult, String condition) {
        int count = 0;

        // If the range of time is <= 14 days
        if(dateEnd == null) {
            // Cycle all the therapies/pressures and find math with startDate and condition (therapy/pressure)
            for (int i = 0; i < queryResult.size() - 2; i = i + 3) {
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
            for (int i = 0; i < queryResult.size() - 2; i = i + 3) {
                // If I found equal date values and equal conditions
                if ((queryResult.get(i+1) != null && (LocalDate.parse(queryResult.get(i)).isAfter(start) && LocalDate.parse(queryResult.get(i)).isBefore(end)) || ((LocalDate.parse(queryResult.get(i)).isEqual(start) || LocalDate.parse(queryResult.get(i)).isEqual(end)))) && queryResult.get(i + 1).equals(condition)) {
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
     *method to take the id of the patient
     * @param idPatient integer
     */
    public static void setPatient(int idPatient) {
        DoctorPlusInfoPatientController.idPatient = idPatient;
    }


}

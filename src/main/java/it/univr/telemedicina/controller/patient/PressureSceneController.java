package it.univr.telemedicina.controller.patient;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.TablePatientPressures;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.models.Pressure;
import it.univr.telemedicina.models.PressureList;
import it.univr.telemedicina.models.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

public class PressureSceneController implements Initializable {
    @FXML
    public AnchorPane pressureScene;
    @FXML
    public TextField txtPresSystolic;
    @FXML
    public TextField txtPresDiastolic;
    @FXML
    public DatePicker datePres;
    @FXML
    public ComboBox boxTimePres;
    @FXML
    public CheckComboBox boxSymptoms;
    @FXML
    public TextField txtOtherSymptoms;
    @FXML
    public ToggleButton weekPresToggle;
    @FXML
    public ToggleButton monthPresToggle;

    // Table pressure
    @FXML
    public TableView <TablePatientPressures> tablePatientPres;
    @FXML
    public TableColumn <TablePatientPressures, LocalDate>  columnDataPresTable;
    @FXML
    public TableColumn <TablePatientPressures, String> columnPressurePresTable;
    @FXML
    public TableColumn <TablePatientPressures, String> columnStatePresTable;
    @FXML
    public TableColumn <TablePatientPressures, String > columnHourPresTable;
    @FXML
    public LineChart<?,?> chartPatientPres;

    private final MainApplication newScene = new MainApplication();
    private static Patient patient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTablePat();

        // Initialize Pressure Page

        displaySymptoms();
        updateGraph(30);
    }

    /**
     * Take patient Data
     * @param patient
     */
    public void setPatient(Patient patient){
        PressureSceneController.patient = patient;
    }

    /**
     * Table View Home
     */
    private void setTablePat() {
        ObservableList<TablePatientPressures> collection = FXCollections.observableArrayList();

        PressureList pressureList = new PressureList();
        TablePatientPressures dati;

        // Check if the list is not null
        if(!pressureList.getPressuresById(patient.getPatientID()).isEmpty()) {
            // Cycle all the pressure of the user and add it to the data collection
            for (Pressure pressure : pressureList.getPressuresById(patient.getPatientID())) {
                dati = new TablePatientPressures(pressure.getDate(), pressure.getHour().toString(), pressure.getSystolicPressure(), pressure.getDiastolicPressure(), pressure.getConditionPressure());
                collection.add(dati);
            }
        }
        else {  // if there arent pressure
            tablePatientPres.setPlaceholder(new Label("Nessuna pressione rilevata in precedenza"));
        }
        //Setting columns
        columnDataPresTable.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnHourPresTable.setCellValueFactory(new PropertyValueFactory<>("hour"));
        columnPressurePresTable.setCellValueFactory(new PropertyValueFactory<>("pressSD"));
        columnStatePresTable.setCellValueFactory(new PropertyValueFactory<>("state"));

        //Set items in table
        tablePatientPres.setItems(collection);
        tablePatientPres.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ##### Methods for Pressure page #####

    /**
     * Register the pressure into the Database
     * @param actionEvent
     */
    public void sendPressuresButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);

            // Create pressure instance
            Pressure pressure = initPressures();
            pressure.insertInDatabase();

            // Send email if ConditionPressure is elevate
            if(!(pressure.getConditionPressure().equals("Ottimale") || pressure.getConditionPressure().equals("Normale") || pressure.getConditionPressure().equals("Normale – alta"))){
                db.insertQuery("Chat", new String[]{"Sender", "Receiver", "Text", "ReadFlag"}, new Object[]{-1, patient.getRefDoc(), "Il paziente " + patient.getName() + " " + patient.getSurname() + " ha registrato una pressione di tipo: " + pressure.getConditionPressure(), 0});
            }

            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente", Alert.AlertType.INFORMATION);


            setTablePat();
            handleTimePresChoose(new ActionEvent());     // Refresh graph
            handleTimePresChoose(new ActionEvent());     // Refresh graph
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove a pressure into the database
     * @param actionEvent
     */
    public void removePressuresButton(ActionEvent actionEvent) {
        try {
            // Create pressure instance
            Pressure pressure = initPressures();
            pressure.removeInDatabase();

            // Remove pressure success
            newScene.showAlert("Invio","Valori eliminati correttamente", Alert.AlertType.INFORMATION);

            // Refresh table
            //collection.removeIf(e -> (e.getDate().isEqual(pressure.getDate()) && (e.getHour() + ":00").equals(pressure.getHour().toString()) && e.getPressSD().equals(pressure.getSystolicPressure() + "/" + pressure.getDiastolicPressure())));

            setTablePat();
            handleTimePresChoose(new ActionEvent());     // Refresh graph
            handleTimePresChoose(new ActionEvent());     // Refresh graph
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for remove PressuresButton and sendPressuresButton
     * @return
     */
    public Pressure initPressures(){
        int systolic;
        int diastolic;
        LocalDate pressureDate;
        String otherSymptoms = "";

        systolic = Integer.parseInt(txtPresSystolic.getText());
        diastolic = Integer.parseInt(txtPresDiastolic.getText());

        //Check otherSymptoms
        if (txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
            otherSymptoms = txtOtherSymptoms.getText();

        } else if (txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
            throw new ParameterException("Campo di testo altri sintomi error");

        pressureDate = datePres.getValue();

        // check correction of values
        Pressure.checkPressuresParameters(systolic, diastolic, pressureDate, (String) boxTimePres.getValue());
        ObservableList<String> symptomsList = boxSymptoms.getCheckModel().getCheckedItems();  //take the symptoms

        StringBuilder symptomString = new StringBuilder();
        if (!symptomsList.isEmpty()) {
            symptomString = new StringBuilder();
            StringBuilder finalSymptomString = symptomString;
            symptomsList.forEach(s -> finalSymptomString.append(s).append(", "));
            symptomString.delete(symptomString.length() - 2, symptomString.length());

            if(!otherSymptoms.isEmpty())
                symptomString.append(", ").append(otherSymptoms);
        }

        return new Pressure(patient.getPatientID(), pressureDate, LocalTime.parse(boxTimePres.getValue() + ":00:00"),systolic,diastolic,symptomString.toString().replace(", Altro", ""));
    }

    /**
     * Update the graph
     * @param dayToTake number of day to take from today, to create table
     */
    private void updateGraph(int dayToTake) {
        ArrayList<String> list;
        LocalDate today = LocalDate.now();
        LocalDate lastPressureToTake = today.minusDays(dayToTake);
        PressureList pressureList = new PressureList();

        // List of pressure transformed in String (that contain all fields of the pressure class)
        list = pressureList.getPressureToString(pressureList.getPressuresByDate(patient.getPatientID(),lastPressureToTake,today));

        chartPatientPres.setTitle("Grafico Pressione");

        XYChart.Series series = new XYChart.Series<>();
        series.setName("Pressione Sistolica");
        XYChart.Series series2 = new XYChart.Series<>();
        series2.setName("Pressione Diastolica");

        // create the list with 7/30 values
        ArrayList<String> dataTaken = new ArrayList<>();
        ArrayList<XYChart.Data> dataSeries = new ArrayList<>();
        ArrayList<XYChart.Data> dataSeries2 = new ArrayList<>();

        // TreeMap of (Date, [Systolic, Diastolic])
        TreeMap<LocalDate, ArrayList<Integer>> pressures = new TreeMap<>();
        for(int i = 0; i < list.size(); i += 7) {   // date,systolic, diastolic,
            // If I already have used the pressure of this day
            if (pressures.containsKey(LocalDate.parse(list.get(i+1)))) {  //ID, DATE, HOUR, SYSTOLIC, DIASTOLIC, SYMP, CONDITION
                int newSystolic = (pressures.get(LocalDate.parse(list.get(i+1))).get(0)) + Integer.parseInt(list.get(i+3));
                int newDiastolic = (pressures.get(LocalDate.parse(list.get(i+1))).get(1)) + Integer.parseInt(list.get(i + 4));
                pressures.put(LocalDate.parse(list.get(i+1)), new ArrayList<>(List.of(newSystolic, newDiastolic)));        // Insert the sum of the both pressures
            } else {

                pressures.put(LocalDate.parse(list.get(i+1)), new ArrayList<>(List.of(Integer.parseInt(list.get(i+3)), Integer.parseInt(list.get(i + 4)))));
            }
        }

        // populating the series with data
        for(LocalDate key : pressures.keySet()) {
            dataSeries.add(new XYChart.Data<>(key.toString(), pressures.get(key).get(0)/list.stream().filter(s -> s.equals(key.toString())).count()));
            dataSeries2.add(new XYChart.Data<>(key.toString(), pressures.get(key).get(1)/list.stream().filter(s -> s.equals(key.toString())).count()));
        }

        // Add the data to the series
        series.getData().addAll(dataSeries);
        series2.getData().addAll(dataSeries2);

        // Add data to graphic
        chartPatientPres.getData().setAll(series, series2);

        // Set line and label colors
        series.getNode().setStyle("-fx-stroke: red;");
        series2.getNode().setStyle("-fx-stroke: blue;");
        chartPatientPres.setStyle("-fx-background-color: white; CHART_COLOR_1: #ff0000; CHART_COLOR_2: #0000FF;");
        chartPatientPres.setCreateSymbols(false);
        chartPatientPres.setAnimated(false);
    }

    /**
     * Method to show in the checkBox the possible Symptoms and the time when the patient register a new pressure.
     */
    private void displaySymptoms() {
        // symptomps list
        boxSymptoms.getItems().addAll("Mal di testa", "Stordimento vertigini", "Ronzii nelle orecchie", "Perdite di sangue dal naso", "Dispnea", "Cardiopalmo", "Ansia", "Angoscia", "Arrossamento cutaneo", "Oliguria", "Altro");
        boxTimePres.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        //listening if "Altro" else is entered
        boxSymptoms.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            if (boxSymptoms.getCheckModel().isChecked("Altro")) {
                txtOtherSymptoms.setVisible(true);
            } else {
                txtOtherSymptoms.setVisible(false);
                txtOtherSymptoms.setText(null);
            }
        });
    }

    /**
     * Method to set the range of day to display, using the UI toggle
     * @param actionEvent
     */
    public void handleTimePresChoose(ActionEvent actionEvent) {
        if(weekPresToggle.isSelected()){
            updateGraph(7);
        }

        else if(monthPresToggle.isSelected())
            updateGraph(30);
    }
}

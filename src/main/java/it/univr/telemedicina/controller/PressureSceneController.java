package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.TablePatientPressures;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.users.Patient;
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

    public void setPatient(Patient patient){
        PressureSceneController.patient = patient;
    }

    //TABLE VIEW HOME
    private void setTablePat() {
        ObservableList<TablePatientPressures> collection = FXCollections.observableArrayList();
        tablePatientPres.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try{
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM BloodPressures WHERE IDPatient = " + patient.getPatientID(),new String[]{"Date","Hour","SystolicPressure", "DiastolicPressure", "ConditionPressure"});
            TablePatientPressures dati;

            //There is no therapy
            if(info.isEmpty()) {
                tablePatientPres.setPlaceholder(new Label("Nessuna pressione rilevata in precedenza"));
                return;
            }

            //Initialize
            for(int i = 0; i < info.size()-4; i = i + 5){
                dati = new TablePatientPressures(LocalDate.parse(info.get(i)), info.get(i+1), Integer.parseInt(info.get(i+2)), Integer.parseInt(info.get(i+3)), info.get(i+4));
                collection.add(dati);
            }
            //Setting columns
            columnDataPresTable.setCellValueFactory(new PropertyValueFactory<>("date"));
            columnHourPresTable.setCellValueFactory(new PropertyValueFactory<>("hour"));
            columnPressurePresTable.setCellValueFactory(new PropertyValueFactory<>("pressSD"));
            columnStatePresTable.setCellValueFactory(new PropertyValueFactory<>("state"));
            //Set items in table
            tablePatientPres.setItems(collection);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // set ITEM
        tablePatientPres.setItems(collection);
    }

    // ##### Methods for Pressure page #####

    public void sendPressuresButton(ActionEvent actionEvent) {
        int systolic;
        int diastolic;
        String otherSymptoms = null;
        LocalDate pressureDate = null;

        try {
            Database db = new Database(2);
            Map<String, Object> dati = initPressures();
            dati.put("IDPatient", patient.getPatientID());
            systolic = (int) dati.get("SystolicPressure");
            diastolic = (int) dati.get("DiastolicPressure");
            dati.put("ConditionPressure", checkPressure(systolic, diastolic));

            //Convert keySet in keyArrayString
            Set<String> key = dati.keySet();
            String[] keyString = key.toArray(new String[0]);
            Collection<Object> values = dati.values();
            Object[] valuesString = values.toArray(new Object[0]);
            System.out.println("Dati values; " + dati.values());
            System.out.println("Key: " + keyString);

            //Query for insert data in BloodPressures
            db.insertQuery("BloodPressures", keyString, valuesString);

            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente", Alert.AlertType.INFORMATION);

            setTablePat();  // Refresh table
            handleTimePresChoose(new ActionEvent());     // Refresh graph
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePressuresButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);
            Map<String, Object> dati = initPressures();
            db.deleteQuery("BloodPressures",dati);

            // Remove pressure success
            newScene.showAlert("Invio","Valori eliminati correttamente", Alert.AlertType.INFORMATION);

            setTablePat();  // Refresh table
            handleTimePresChoose(new ActionEvent());     // Refresh graph
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
            throw new RuntimeException(e);
        }
    }

    // Method for removePressuresButton and sendPressuresButton
    public Map<String,Object> initPressures(){
        int systolic;
        int diastolic;
        LocalDate pressureDate;
        String otherSymptoms = "";

        //Key --> fields, Values --> values
        Map<String, Object> dati = new TreeMap<>();
        systolic = Integer.parseInt(txtPresSystolic.getText());
        diastolic = Integer.parseInt(txtPresDiastolic.getText());

        //Check otherSymptoms
        if (txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
            otherSymptoms = txtOtherSymptoms.getText();

        } else if (txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
            throw new ParameterException("Campo di testo altri sintomi error");
        pressureDate = datePres.getValue();

        // check correction of values
        checkPressuresParameters(systolic, diastolic, pressureDate, (String) boxTimePres.getValue());
        ObservableList<String> symptomsList = boxSymptoms.getCheckModel().getCheckedItems();  //take the symptoms
        StringBuilder symptomString;
        if (symptomsList.isEmpty())
            symptomString = new StringBuilder();
        else {
            symptomString = new StringBuilder();
            symptomsList.forEach(s -> symptomString.append(s).append(", "));
            symptomString.delete(symptomString.length() - 2, symptomString.length());
            if(!otherSymptoms.isEmpty())
                symptomString.append(", ").append(otherSymptoms);
        }
        dati.put("SystolicPressure",systolic);
        dati.put("DiastolicPressure",diastolic);
        dati.put("Date",pressureDate);
        dati.put("Symptoms",symptomString.toString().replace(", Altro", ""));
        dati.put("Hour", boxTimePres.getValue() + ":00:00");

        return dati;
    }
    public void checkPressuresParameters(int systolic, int diastolic, LocalDate datePress, String time) {
        // check diastolic
        if (diastolic <= 0 || diastolic >= 150)
            throw new ParameterException("Pressione diastolica error");

        // check systolic
        if (systolic <= 0 || systolic >= 250)
            throw new ParameterException("Sistolica errore");

        if (systolic <= diastolic)
            throw new ParameterException("Confronto errore");

        // check if the mensuration date is right
        if (datePress.isAfter(LocalDate.now()))
            throw new ParameterException("Data errore");
        if(datePress.isEqual(LocalDate.now())){
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < Integer.parseInt((String) boxTimePres.getValue())){
                throw new ParameterException("Ora errore");
            }
        }
        if(time.isEmpty())
            throw new ParameterException("Orario errore");
    }

    private String checkPressure(int systolic, int diastolic){
        ArrayList<String> category = new ArrayList<>(Arrays.asList("Ottimale","Normale","Normale - alta","Ipertensione di Grado 1 borderline","Ipertensione di Grado 1 lieve","Ipertensione di Grado 2 moderata", "Ipertensione di Grado 3 grave", "Ipertensione sistolica isolata borderline", "Ipertensione sistolica isolata"));
        ArrayList<Integer> valuesSystolic = new ArrayList<>(Arrays.asList(120, 130, 139, 149, 159, 179, 180, 250));    //140-149,>=150
        ArrayList<Integer> valuesDiastolic = new ArrayList<>(Arrays.asList(0, 80, 85, 89, 94, 99, 109, 110));    //<90
        int index = -1;

        //Check systolic
        for(Integer value : valuesSystolic){
            if(value >= systolic) {
                index = valuesSystolic.indexOf(value);
                //Special case
                if(index == 3 && diastolic < 90)
                    return category.get(7);
                if(index > 3 && diastolic < 90)
                    return category.get(8);
                break;
            }
        }

        //Parto direttamente dall'index
        //check diastolic
        for(int i = index; i < valuesDiastolic.size(); i++)
        {
            if(valuesDiastolic.get(i) >= diastolic){
                return category.get(i);
            }
        }

        // Example systolic = 300
        return "Valori fuori norma";
    }

    private void updateGraph(int dayToTake) {
        ArrayList<String> list;
        LocalDate today = LocalDate.now();
        LocalDate lastPressureToTake = today.minusDays(dayToTake);

        // take pressure from database
        try {
            Database db = new Database(2);
            list = db.getQuery("SELECT SystolicPressure, DiastolicPressure, Date FROM BloodPressures WHERE IDPatient = " + patient.getPatientID() + " AND Date BETWEEN '" + lastPressureToTake.toString() + " 00:00:00' AND '" + today.toString() + " 00:00:00' ORDER BY ID DESC", new String[]{"SystolicPressure", "DiastolicPressure", "Date"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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
        for(int i = 0; i < list.size(); i += 3) {   // systolic, diastolic, date
            // If I already have used the pressure of this day
            if (pressures.containsKey(LocalDate.parse(list.get(i + 2)))) {
                int newSystolic = (pressures.get(LocalDate.parse(list.get(i + 2))).get(0)) + Integer.parseInt(list.get(i));
                int newDiastolic = (pressures.get(LocalDate.parse(list.get(i + 2))).get(1)) + Integer.parseInt(list.get(i + 1));
                pressures.put(LocalDate.parse(list.get(i + 2)), new ArrayList<>(List.of(newSystolic, newDiastolic)));        // Insert the sum of the both pressures
            } else {
                pressures.put(LocalDate.parse(list.get(i + 2)), new ArrayList<>(List.of(Integer.parseInt(list.get(i)), Integer.parseInt(list.get(i + 1)))));
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
    }

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

    public void handleTimePresChoose(ActionEvent actionEvent) {
        if(weekPresToggle.isSelected())
            updateGraph(7);
        else if(monthPresToggle.isSelected())
            updateGraph(30);
    }
}

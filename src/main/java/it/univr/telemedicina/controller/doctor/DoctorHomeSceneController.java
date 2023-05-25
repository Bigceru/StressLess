package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.chart.XYChart.Data;


import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Class to control the DoctorHomeScene
 */
public class DoctorHomeSceneController implements Initializable {
    @FXML
    public Label lblCountPatients;
    @FXML
    public Button buttonSendText;
    @FXML
    public Button buttonMailBox;
    @FXML
    public Circle lblNewMail;
    @FXML
    public Button inspectPatientButton;

    // Table variable
    @FXML
    public TableView<Patient> tablePatient;
    @FXML
    public TableColumn<?, ?> columnNamePatientTable;
    @FXML
    public TableColumn<?, ?> columnStatePatientTable;

    // Doctor instance
    private static Doctor doctor;
    @FXML
    public BarChart<String, Integer> barChartNewPatients;
    @FXML
    public DatePicker buttonStartRegistration;
    @FXML
    public DatePicker buttonEndRegistration;
    @FXML
    public Button buttonSearchRegistration;
    @FXML
    public CategoryAxis xAxis;
    private final MainApplication newScene = new MainApplication();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTablePatients();
    }

    public void showNewRegistration(ActionEvent actionEvent){
        if(buttonStartRegistration.getValue() != null && buttonEndRegistration.getValue() != null) {
            buttonEndRegistration.setStyle("-fx-text-fill: black;");
            buttonStartRegistration.setStyle("-fx-text-fill: black;");
            setBarChartNewPatients();
        }
        else{
            buttonEndRegistration.setStyle("-fx-text-fill: red;");
            buttonStartRegistration.setStyle("-fx-text-fill: red;");
        }
    }




     //Method to initialize the table and the table columns with the Patient therapies

    public void setTablePatients(){
        ObservableList<Patient> collection = FXCollections.observableArrayList();   // Collection of data to insert in the table

        try{
            // Open database hospital and get list of patients with the doctor id
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT ID, name, surname FROM Patients WHERE refDoc = " + doctor.getID(), new String[]{"ID", "name", "surname"});
            ArrayList<String> infoTherapies;
            lblCountPatients.setText(info.size()/3 + " "); //Number of patients

            // If there is no therapy
            if(info.isEmpty()) {
                tablePatient.setPlaceholder(new Label("Nessun paziente trovato"));
                return;
            }
            else {  // If there are therapies for the patients
                StringBuilder queryString = new StringBuilder("SELECT IDPatient, TherapyName FROM Therapies WHERE IDPatient = ");

                // Cycle all IDPatient
                for(int i = 0; i < info.size()-2; i += 3) {
                    queryString.append(info.get(i)).append(" OR IDPatient = ");
                }

                // Remove the last unused OR
                queryString.delete(queryString.length()-15, queryString.length()-1);

                // Query for Therapies
                infoTherapies = db.getQuery(queryString.toString(), new String[]{"IDPatient", "TherapyName"});
            }

            // Initialize the data to insert in the table
            for(int i = 0; i < info.size()-2; i += 3){
                StringBuilder terapie = new StringBuilder();

                // Cycle the list of therapies and save the therapies of the patient with id info.get(i)
                for(int j = 0; j < infoTherapies.size()-1; j += 2) {
                    if(infoTherapies.get(j).equals(info.get(i))) {
                        terapie.append(infoTherapies.get(j + 1)).append(", ");
                        System.out.println(infoTherapies.get(j + 1));
                    }
                }

                // If there is one therapies
                if(terapie.isEmpty())
                    terapie.append("Non hai terapie in corso");
                else
                    terapie.delete(terapie.length()-2, terapie.length()-1);

                // Add the patient and the therapies to the collection
                collection.add(new Patient(info.get(i+1), info.get(i+2), terapie.toString(), null));
            }

            // Setting columns data
            columnNamePatientTable.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnStatePatientTable.setCellValueFactory(new PropertyValueFactory<>("therapy"));

            // Set items in table
            tablePatient.setItems(collection);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Set collection of data to the table
        tablePatient.setItems(collection);

        tablePatient.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setBarChartNewPatients() {
        ArrayList<String> list;
        LocalDate start = buttonStartRegistration.getValue();
        LocalDate end = buttonEndRegistration.getValue();

        //Check date
        if(start.isAfter(end)){
            buttonEndRegistration.setStyle("-fx-text-fill: red;");
            buttonStartRegistration.setStyle("-fx-text-fill: red;");
            return;
        }
        else {
            buttonEndRegistration.setStyle("-fx-text-fill: black;");
            buttonStartRegistration.setStyle("-fx-text-fill: black;");
        }

        try {
            System.out.print("Prima della query");
            Database db = new Database(2);
            list = db.getQuery("SELECT registration FROM Patients WHERE refDoc = " + doctor.getID() + " AND Registration BETWEEN '" + start + "' AND '" + end.toString() + "'", new String[]{"registration"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        barChartNewPatients.setTitle("Istrogramma nuovi pazienti");
        barChartNewPatients.lookup(".chart-title").setStyle("-fx-text-fill: white;");

        XYChart.Series<String, Integer> xSeries = new XYChart.Series<>();
        xSeries.setName("Registrazioni");

        // Collect data to add further to axis
        TreeMap<LocalDate, Integer> dataTaken = new TreeMap<>();

        // Insert all the date into dataTaken
        start.datesUntil(end).forEach(localDate -> dataTaken.put(localDate, 0));
        dataTaken.put(end, 0);

        // Cycle the list of dates to add new registration event
        for(String dataString : list) {
            LocalDate date = LocalDate.parse(dataString);
            dataTaken.put(date, dataTaken.get(date) + 1);
        }

        System.out.print("Ecco la lista dei giorni: \n");
        dataTaken.forEach((localDate, integer) -> System.out.println(localDate.toString() + " --> " + integer + "\n"));

        if(ChronoUnit.DAYS.between(start, end) <= 7 ){
            dataTaken.forEach((localDate, integer) -> xSeries.getData().add(new XYChart.Data<>(localDate.toString(), integer)));
        } else if (ChronoUnit.DAYS.between(start, end) <= 31) {
            int somma = 0;
            int i = 0;
            for(LocalDate key : dataTaken.keySet()){
                somma += dataTaken.get(key);

                if(i % 7 == 0 && i != 0){
                    xSeries.getData().add(new XYChart.Data<>(i/7 + " Settimana", somma));
                    System.out.println("Settimana " + (i/7) + " Somma : " + somma );
                    somma = 0;
                }
                i++;
            }
        }

        // Add data to graphic
        barChartNewPatients.getData().setAll(xSeries);
        for(Node n:barChartNewPatients.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: #6ea483;-fx-text-fill: white");
        }

        //CHART_COLOR_1: #ff0000;
        barChartNewPatients.setStyle("CHART_COLOR_1: #6ea483;");

        barChartNewPatients.setAnimated(false);     // Remove the fucking animation to print right label

    }

    public void setDoctor(Doctor doctor){
        DoctorHomeSceneController.doctor = doctor;
    }
}
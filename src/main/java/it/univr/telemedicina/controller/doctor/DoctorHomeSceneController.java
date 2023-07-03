package it.univr.telemedicina.controller.doctor;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.models.TherapyList;
import it.univr.telemedicina.models.users.Doctor;
import it.univr.telemedicina.models.users.Patient;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.TherapyFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Class to control the DoctorHomeScene
 */
public class DoctorHomeSceneController implements Initializable {

    private final MainApplication newScene = new MainApplication();
    @FXML
    public Label lblCountPatients;
    @FXML
    public Button buttonSendText;
    @FXML
    public Button buttonMailBox;
    @FXML
    public FontAwesomeIcon emailIcon;
    @FXML
    public Circle iconNewMail;

    // Table variable
    @FXML
    public TableView<Patient> tablePatient;
    @FXML
    public TableColumn<?, ?> columnNamePatientTable;
    @FXML
    public TableColumn<?, ?> columnStatePatientTable;
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
    @FXML
    public Label lblTableTitle;

    // Doctor instance
    private static Doctor doctor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Database database = new Database(2);

            // Query to set emailIcon
            ArrayList<String> messageToReadQuery = database.getQuery("SELECT ReadFlag FROM Chat WHERE Receiver = " + doctor.getID() + " AND ReadFlag = 0", new String[]{"ReadFlag"});

            // If there are no new messages
            iconNewMail.setVisible(!messageToReadQuery.isEmpty());

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setTablePatients();
    }

    /**
     * check the data validation
     * @param actionEvent actionEvent
     */
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

    /**
     *  initialize the table and the table columns with the Patient therapies
     */
    public void setTablePatients(){
        ObservableList<Patient> collection = FXCollections.observableArrayList();   // Collection of data to insert in the table

        try{
            // Open database hospital and get list of patients with the doctor id
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT ID, name, surname FROM Patients WHERE refDoc = " + doctor.getID(), new String[]{"ID", "name", "surname"});
            ArrayList<String> resultQueryTherapies = new ArrayList<>();
            lblCountPatients.setText(info.size()/3 + " "); //Number of patients

            // If there is no therapy
            if(info.isEmpty()) {
                tablePatient.setPlaceholder(new Label("Nessun paziente trovato"));
                return;
            }
            else {  // If there are therapies for the patients
                TherapyList therapyList = new TherapyList();

                // Cycle all IDPatient and get therapies
                for(int i = 0; i < info.size() - 2; i += 3) {
                    resultQueryTherapies.addAll(therapyList.getWhatUWantString(therapyList.getTherapyToString(therapyList.getCurrentTherapy(Integer.parseInt(info.get(i)))), new TherapyFields[]{TherapyFields.ID_PATIENT, TherapyFields.THERAPY_NAME}));
                }
            }

            // Initialize the data to insert in the table
            for(int i = 0; i < info.size() - 2; i += 3){
                StringBuilder terapie = new StringBuilder();

                // Cycle the list of therapies and save the therapies of the patient with id info.get(i)
                for(int j = 0; j < resultQueryTherapies.size()-1; j += 2) {
                    if(resultQueryTherapies.get(j).equals(info.get(i))) {
                        terapie.append(resultQueryTherapies.get(j + 1)).append(", ");
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

    /**
     * Shows a bar graph to analyze the increase of patients related to the doctor
     */
    private void setBarChartNewPatients() {
        ArrayList<String> list;
        LocalDate start = buttonStartRegistration.getValue();
        LocalDate end = buttonEndRegistration.getValue();

        //Check date
        if(end == null || start == null || start.isAfter(end)){
            buttonEndRegistration.setStyle("-fx-text-fill: red;");
            buttonStartRegistration.setStyle("-fx-text-fill: red;");
            return;
        }
        else {
            buttonEndRegistration.setStyle("-fx-text-fill: black;");
            buttonStartRegistration.setStyle("-fx-text-fill: black;");
        }

        try {
            Database db = new Database(2);
            list = db.getQuery("SELECT registration FROM Patients WHERE refDoc = " + doctor.getID() + " AND Registration BETWEEN '" + start + "' AND '" + end + "'", new String[]{"registration"});
            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        lblTableTitle.setVisible(false);    // Hide the temporary table title
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

        if(ChronoUnit.DAYS.between(start, end) <= 7 ){
            dataTaken.forEach((localDate, integer) -> xSeries.getData().add(new XYChart.Data<>(localDate.toString(), integer)));

        } else if (ChronoUnit.DAYS.between(start, end) <= 31) {
            int somma = 0;
            int i = 0;
            for(LocalDate key : dataTaken.keySet()){
                somma += dataTaken.get(key);

                if(dataTaken.size()-1 == i){
                    xSeries.getData().add(new XYChart.Data<>(i/7+1   + " Settimana", somma));
                    somma = 0;
                }

                if((i % 7 == 0 && i != 0)  ){
                    xSeries.getData().add(new XYChart.Data<>(i/7  + " Settimana", somma));
                    somma = 0;
                }
                i++;
            }

        } else {
            int somma = 0;
            int i = 0;

            for(LocalDate key : dataTaken.keySet()){
                somma += dataTaken.get(key);

                if(dataTaken.size()-1 == i){
                    xSeries.getData().add(new XYChart.Data<>(i/30+1   + " Mese", somma));
                    somma = 0;
                }
                if(i % 30 == 0 && i != 0){
                    xSeries.getData().add(new XYChart.Data<>(i/30 + " Mese", somma));
                    somma = 0;
                }
                i++;
            }
        }

        //Add number on
        xSeries.getData().forEach(data -> {
            Label label = new Label(data.getYValue().toString());
            label.setAlignment(Pos.TOP_CENTER);
            label.setStyle("-fx-font-size: 16px; -fx-text-fill: white");
            data.setNode(label);
            data.getNode().setNodeOrientation(NodeOrientation.INHERIT);
        });

        // Add data to graphic
        barChartNewPatients.getData().setAll(xSeries);
        for(Node n:barChartNewPatients.lookupAll(".default-color0.chart-bar")) {
            n.setStyle("-fx-bar-fill: #6ea483;-fx-text-fill: white");
        }

        //CHART_COLOR_1: #ff0000;
        barChartNewPatients.setStyle("CHART_COLOR_1: #6ea483;");

        barChartNewPatients.setAnimated(false);     // Remove the fucking animation to print right label

    }

    /**
     * Create the Chat Page
     * @param actionEvent actionEvent
     * @throws IOException exception
     */
    @FXML
    private void handleChatButton(ActionEvent actionEvent) throws IOException {
        iconNewMail.setVisible(false);
        newScene.addScene("/it/univr/telemedicina/chatPages/ChatMenu.fxml");
    }

    /**
     * allows to take the doctor Data
     * @param doctor doctor
     */
    public void setDoctor(Doctor doctor){
        DoctorHomeSceneController.doctor = doctor;
    }
}
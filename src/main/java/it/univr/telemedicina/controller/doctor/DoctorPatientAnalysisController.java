package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.*;
import it.univr.telemedicina.models.Pressure;
import it.univr.telemedicina.models.PressureList;
import it.univr.telemedicina.models.Therapy;
import it.univr.telemedicina.models.TherapyList;
import it.univr.telemedicina.models.users.Doctor;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DoctorPatientAnalysisController implements Initializable {

    MainApplication newScene = new MainApplication();
    @FXML
    public Line lineAnalysisScene;
    @FXML
    public AnchorPane patientAnalysisPane;
    @FXML
    private ComboBox<String> boxPatientList;
    @FXML
    private Label lblTherapyStatus;
    @FXML
    private Label lblTrendTherapy;
    @FXML
    public TableView<TablePatientPressures> tableSearchPatient;
    @FXML
    public TableColumn <TablePatientPressures,String> columnState;
    @FXML
    public TableColumn<TablePatientPressures,LocalDate> columnDate;
    @FXML
    public TableColumn<TablePatientPressures,String> columnSymptoms;
    @FXML
    public TableColumn<TablePatientPressures,String> columnPressure;
    @FXML
    public TableColumn<TablePatientPressures,String> columnHour;

    // Doctor instance
    private static Doctor doctor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set line size property
        //lineAnalysisScene.endXProperty().bind(patientAnalysisPane.widthProperty().subtract(12));
        //lineAnalysisScene.setEndY(lineAnalysisScene.getStartY());

        // Initialize value for boxPatientList
        try {
            Database database = new Database(2);
            ArrayList<String> queryResult = database.getQuery("SELECT ID, name, surname FROM  Patients WHERE refDoc = " + doctor.getID(), new String[]{"ID", "name", "surname"});
            database.closeAll();

            // If query have some results
            if(!queryResult.isEmpty()) {
                // Cycle all the Patients and add them to comboBox
                for(int i = 0; i < queryResult.size()-2; i += 3)
                    boxPatientList.getItems().add(queryResult.get(i) + " - " + queryResult.get(i+1) + " " + queryResult.get(i+2));
            }
            else
                boxPatientList.getItems().add("Non hai nessun paziente");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to handle Search Button click
     * @param actionEvent actionEvent
     */
    public void handleSearchButton(ActionEvent actionEvent){
        int idPatient;

        if(boxPatientList.getValue() != null  && boxPatientList.getValue().compareTo("Non hai nessun paziente") != 0) {
            idPatient = Integer.parseInt(boxPatientList.getValue().split("-")[0].trim());

            TherapyList therapyList = new TherapyList();

            fillTherapy(therapyList.getTherapyToString(therapyList.getCurrentTherapy(idPatient)));    // Initialize value for therapy label
            setTable(idPatient);
        }
        else {
            // Error
            newScene.showAlert("ComboBox vuota","Inserire un valore nella combo box", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show the therapy of the patient
     * @param queryResultTherapy result of query from database
     */
    private void fillTherapy(ArrayList<String> queryResultTherapy){
        // if the query is empty, patient is not in therapy
        if(queryResultTherapy.isEmpty()) {
            lblTherapyStatus.setText("NON ATTUALMENTE IN TERAPIA");
            lblTrendTherapy.setVisible(false);
        }
        else {           // Setting parameters
            lblTherapyStatus.setText("ATTUALMENTE IN TERAPIA");
            StringBuilder trendTherapy = new StringBuilder();
            boolean result;

            // Cycle all the therapies and check if the Patient has followed them right
            for(int i = 0; i < queryResultTherapy.size()-7; i += 8) {

               Therapy bill = new Therapy(Integer.parseInt(queryResultTherapy.get(i)), queryResultTherapy.get(i+1), queryResultTherapy.get(i+2), Integer.parseInt(queryResultTherapy.get(i+3)), Integer.parseInt(queryResultTherapy.get(i+4)), queryResultTherapy.get(i+5), LocalDate.parse(queryResultTherapy.get(i+6)), LocalDate.parse(queryResultTherapy.get(i+7)));
               result = bill.checkTherapy();

               // IF the result of the check is false
               if(!result)
                   trendTherapy.append(queryResultTherapy.get(i + 1)).append(" (").append(queryResultTherapy.get(i + 2)).append(")").append(": Data ").append(queryResultTherapy.get(i + 6)).append(" - ").append(queryResultTherapy.get(i + 7)).append(": Non seguita correttamente\n");
               else
                   trendTherapy.append(queryResultTherapy.get(i + 1)).append(" (").append(queryResultTherapy.get(i + 2)).append(")").append(": Data ").append(queryResultTherapy.get(i + 6)).append(" - ").append(queryResultTherapy.get(i + 7)).append(": Seguita correttamente\n");
            }

            lblTrendTherapy.setVisible(true);
            lblTrendTherapy.setText(String.valueOf(trendTherapy));
        }
    }

    /**
     * Fill the Table with patient's information
     * @param idPatient id of the patient chosen
     */
    private void setTable(int idPatient){

        columnDate.prefWidthProperty().bind(tableSearchPatient.widthProperty().divide(6)); // w * 1/4
        columnHour.prefWidthProperty().bind(tableSearchPatient.widthProperty().divide(12)); // w * 1/8
        columnPressure.prefWidthProperty().bind(tableSearchPatient.widthProperty().divide(10)); // w * 1/8
        columnSymptoms.prefWidthProperty().bind(tableSearchPatient.widthProperty().divide(3)); // w * 1/4
        columnState.prefWidthProperty().bind(tableSearchPatient.widthProperty().divide(3)); // w * 1/4
        PressureList pressureList = new PressureList();

        ArrayList<Pressure> list = pressureList.getPressuresById(idPatient);

        // Collection of all TablePatientPressures
        ObservableList<TablePatientPressures> collection = FXCollections.observableArrayList();

        // if the patient have measurements, fill the table
        if (!list.isEmpty())
        {
            TablePatientPressures table;

            // Cycle to get all the query results and add them to table
            for(Pressure pressure : list){
                table = new TablePatientPressures(pressure.getDate(), pressure.getHour().toString(), pressure.getSystolicPressure(), pressure.getDiastolicPressure(), pressure.getSymptoms(), pressure.getConditionPressure());
                collection.add(table);
            }

            columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            columnHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
            columnPressure.setCellValueFactory(new PropertyValueFactory<>("pressSD"));
            columnSymptoms.setCellValueFactory(new PropertyValueFactory<>("symptomps"));
            columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
            }
            else{
                tableSearchPatient.setPlaceholder(new Label("Non ci sono misurazioni registrate"));
            }

            tableSearchPatient.setItems(collection);
            //tableSearchPatient.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    /**
     * Take the doctor Data
     * @param doctor doctor
     */
    public void setDoctor(Doctor doctor){
        DoctorPatientAnalysisController.doctor = doctor;
    }

    /**
     * Show other info about the patient chosen
     * @param actionEvent actionEvent
     * @throws IOException Exception
     */
    public void handleInfoButton(ActionEvent actionEvent) throws IOException {
        int idPatient;
        if(boxPatientList.getValue() != null && boxPatientList.getValue().compareTo("Non hai nessun paziente") != 0) {
            idPatient = Integer.parseInt(boxPatientList.getValue().split("-")[0].trim());
            DoctorPlusInfoPatientController.setPatient(idPatient);
            newScene.addScene("/it/univr/telemedicina/doctorPages/DoctorPlusInfoPatient.fxml");
        }
        else {
            // Error
            newScene.showAlert("ComboBox vuota","Inserire un valore nella combo box", Alert.AlertType.ERROR);
        }
    }

    /**
     * Show the Therapy Page
     * @param actionEvent action
     * @throws IOException Exception
     */
    public void handleTherapyButton(ActionEvent actionEvent) throws IOException{
        if(boxPatientList.getValue() != null && boxPatientList.getValue().compareTo("Non hai nessun paziente") != 0) {
            int idPatient = Integer.parseInt(boxPatientList.getValue().split("-")[0].trim());
            DoctorManageTherapy.setPatient(idPatient);
            newScene.addScene("/it/univr/telemedicina/doctorPages/DoctorManageTherapy.fxml");
        }
        else {
            // Error
            newScene.showAlert("ComboBox vuota","Inserire un valore nella combo box", Alert.AlertType.ERROR);
        }
    }
}

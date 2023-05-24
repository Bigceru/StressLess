package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTablePatients();
    }

    /**
     * Method to initialize the table and the table columns with the Patient therapies
     */
    public void setTablePatients(){
        ObservableList<Patient> collection = FXCollections.observableArrayList();   // Collection of data to insert in the table

        try{
            // Open database hospital and get list of patients with the doctor id
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT ID, name, surname FROM Patients WHERE refDoc = " + doctor.getID(), new String[]{"ID", "name", "surname"});
            ArrayList<String> infoTherapies;
            lblCountPatients.setText(info.size()/3 + ""); //Number of patients

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

    /**
     * Method to set doctor's ID
     * @param doctor doctor's ID
     */
    public void setDoctor(Doctor doctor){
        DoctorHomeSceneController.doctor = doctor;
    }
}

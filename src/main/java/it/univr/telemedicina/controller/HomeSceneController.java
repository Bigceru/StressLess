package it.univr.telemedicina.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.TablePatientDrugs;
import it.univr.telemedicina.Therapy;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeSceneController implements Initializable {

    private final MainApplication newScene = new MainApplication();
    @FXML
    public Label lblPressure;
    @FXML
    public FontAwesomeIcon emailIcon;
    @FXML
    private Label lblRefDoc;
    @FXML
    public Label lblLastPressure;
    @FXML
    public Label lblTherapyState;
    @FXML
    public TableView<TablePatientDrugs> tableTherapies;
    @FXML
    public TableColumn<TablePatientDrugs, String> columnName;
    @FXML
    public TableColumn<TablePatientDrugs, String> columnInstruction;
    @FXML
    public TableColumn<TablePatientDrugs, Integer> columnAmount;
    @FXML
    public TableColumn<TablePatientDrugs, Integer> columnDoses;
    private static Patient patient;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Take patient therapies and check if the patient is following it right
        systemMessage();

        // Set doctor's name/surname label, pressure label, emailIcon
        try {
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM Doctors WHERE ID = " + patient.getRefDoc(), new String[]{"Name","Surname"});    // Query to get doctor name
            lblRefDoc.setText("Dr. " + info.get(0) + " " + info.get(1));

            // Add last pressure value to the label
            info = db.getQuery("SELECT SystolicPressure, DiastolicPressure, Date FROM BloodPressures WHERE IDPatient = " + patient.getPatientID() + " ORDER BY ID DESC", new String[]{"SystolicPressure", "DiastolicPressure", "Date"});
            if(info.isEmpty()) {
                lblPressure.setText("--/--");
                lblLastPressure.setText("--/--");
            }
            else {
                lblPressure.setText(info.get(0) + "/" + info.get(1));
                lblLastPressure.setText(info.get(2));
            }

            /*
            // Query to set emailIcon
            ArrayList<String> messageToReadQuery = db.getQuery("SELECT ReadFlag FROM Chat WHERE Receiver = " + patient.getPatientID() + " AND ReadFlag = 0", new String[]{"ReadFlag"});

            // If there are no new messages
            if(messageToReadQuery.isEmpty()) {
                emailIcon.setFill(Color.WHITE);
            }
            else {
                emailIcon.setFill(Color.RED);
            }
             */

            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setTable();
    }

    private void setTable() {
        ObservableList<TablePatientDrugs> therapy = FXCollections.observableArrayList();

        columnName.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/3
        columnDoses.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/6
        columnAmount.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/6
        columnInstruction.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/3

        try{
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM Therapies WHERE IDPatient = " + patient.getPatientID(),new String[]{"DrugName","DailyDoses","AmountTaken","Instructions"});
            TablePatientDrugs dati;

            //There is no therapy
            if(info.isEmpty()) {
                lblTherapyState.setText("FUORI TERAPIA");
                tableTherapies.setPlaceholder(new Label("Non devi assumere farmaci"));
                return;
            }
            else
                lblTherapyState.setText("IN TERAPIA");

            //Initialize
            for(int i = 0; i < info.size()-3; i = i + 4){
                dati = new TablePatientDrugs(info.get(i), Integer.parseInt(info.get(i+1)), Integer.parseInt(info.get(i+2)),info.get(i+3));
                therapy.add(dati);
            }

            //Setting columns
            columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnDoses.setCellValueFactory(new PropertyValueFactory<>("dose"));
            columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            columnInstruction.setCellValueFactory(new PropertyValueFactory<>("instruction"));

            //Set items in table
            tableTherapies.setItems(therapy);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleChatButton(ActionEvent actionEvent) throws IOException {
        newScene.addScene("/it/univr/telemedicina/chatPages/ChatMenu.fxml");
    }

    private void systemMessage(){
        Therapy therapy = new Therapy();

        try {
            Database database = new Database(2);
            ArrayList<String> resultTherapyQuery =  database.getQuery("SELECT TherapyName, DrugName, DailyDoses, AmountTaken, Instructions FROM Therapies WHERE IDPatient = " + patient.getPatientID() + " AND EndDate >= '" + LocalDate.now() +  "' AND  StartDate <= '" + LocalDate.now().minusDays(3)+ "'", new String[]{"TherapyName", "DrugName", "DailyDoses", "AmountTaken", "Instructions"});

            // Cycle all the therapies fo the Patient
            for(int i = 0; i < resultTherapyQuery.size()-4; i += 5){
                // check for each therapy if patient do the right thing
                boolean check =  therapy.checkTherapy(patient.getPatientID(), resultTherapyQuery.get(i),resultTherapyQuery.get(i+1),Integer.parseInt(resultTherapyQuery.get(i+2)) , Integer.parseInt(resultTherapyQuery.get(i+3)),resultTherapyQuery.get(i+4),LocalDate.now(),LocalDate.now().minusDays(3));

                if(!check) {
                    // Query to insert the new alert message in database
                    database.insertQuery("Chat", new String[]{"Sender", "Receiver", "Text", "ReadFlag"}, new Object[]{-1, patient.getPatientID(), "Non stai seguendo in modo corretto la terapia (" + resultTherapyQuery.get(i) + ")", 0});
                }
            }

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPatient(Patient patient) {HomeSceneController.patient = patient;}

    public void setColor(Color color){
        emailIcon.setFill(color);
    }
}

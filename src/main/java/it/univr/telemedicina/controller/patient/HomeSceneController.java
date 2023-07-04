package it.univr.telemedicina.controller.patient;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.TablePatientDrugs;
import it.univr.telemedicina.models.Therapy;
import it.univr.telemedicina.models.Pressure;
import it.univr.telemedicina.models.PressureList;
import it.univr.telemedicina.models.TherapyList;
import it.univr.telemedicina.models.users.Patient;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.TherapyFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Circle;
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
    public Circle iconNewMail;
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

        // Set doctor's name/surname label, pressure label, iconNewMail
        try {
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM Doctors WHERE ID = " + patient.getRefDoc(), new String[]{"Name","Surname"});    // Query to get doctor name
            lblRefDoc.setText("Dr. " + info.get(0) + " " + info.get(1));

            // Add last pressure value to the label
            PressureList pressureList = new PressureList();
            Pressure pressure;

            if(pressureList.getPressuresById(patient.getPatientID()).isEmpty() /*== null*/) {
                lblPressure.setText("--/--");
                lblLastPressure.setText("--/--");
            }
            else {
                pressure = pressureList.getLastPressure(patient.getPatientID());
                lblPressure.setText(pressure.getSystolicPressure() + "/" + pressure.getDiastolicPressure());
                lblLastPressure.setText(pressure.getDate().toString());
            }

            // Query to set iconNewMail
            ArrayList<String> messageToReadQuery = db.getQuery("SELECT ReadFlag FROM Chat WHERE Receiver = " + patient.getPatientID() + " AND ReadFlag = 0", new String[]{"ReadFlag"});

            // If there are no new messages
            iconNewMail.setVisible(!messageToReadQuery.isEmpty());

            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setTable();
    }

    /**
     * Set Table for the patient
     */
    private void setTable() {
        ObservableList<TablePatientDrugs> therapy = FXCollections.observableArrayList();

        columnName.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/3
        columnDoses.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/6
        columnAmount.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/6
        columnInstruction.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/3

        TherapyList therapyList = new TherapyList();

        ArrayList<String> queryResult = therapyList.getWhatUWantString(therapyList.getTherapyToString(therapyList.getCurrentTherapy(patient.getPatientID())), new TherapyFields[]{TherapyFields.DRUG_NAME, TherapyFields.DAILY_DOSES, TherapyFields.AMOUNT_TAKEN, TherapyFields.INSTRUCTIONS});

        TablePatientDrugs dati;

        //There is no therapy
        if(queryResult.isEmpty()) {
            lblTherapyState.setText("FUORI TERAPIA");
            tableTherapies.setPlaceholder(new Label("Non devi assumere farmaci"));
            return;
        }
        else
            lblTherapyState.setText("IN TERAPIA");

        // Initialize
        for(int i = 0; i < queryResult.size()-3; i = i + 4){
            dati = new TablePatientDrugs(queryResult.get(i), Integer.parseInt(queryResult.get(i+1)), Integer.parseInt(queryResult.get(i+2)),queryResult.get(i+3));
            therapy.add(dati);
        }

        //Setting columns
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDoses.setCellValueFactory(new PropertyValueFactory<>("dose"));
        columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        columnInstruction.setCellValueFactory(new PropertyValueFactory<>("instruction"));

        //Set items in table
        tableTherapies.setItems(therapy);
    }

    /**
     * Open the Chat
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    private void handleChatButton(ActionEvent actionEvent) throws IOException {
        iconNewMail.setVisible(false);
        newScene.addScene("/it/univr/telemedicina/chatPages/ChatMenu.fxml");
    }

    /**
     * Message form the system
     */
    private void systemMessage(){
        //Therapy therapy = new Therapy();
        TherapyList therapyList = new TherapyList();

        try {
            Database database = new Database(2);

            ArrayList<Therapy> resultTherapyQuery = therapyList.getCurrentTherapy(patient.getPatientID());
            // therapyList.getTherapyByDate(patient.getPatientID(), LocalDate.now().minusDays(3), LocalDate.now());

            // Cycle all the therapies fo the Patient
            for(Therapy therapy : resultTherapyQuery){
                // check for each therapy if patient do the right thing
                boolean check = therapy.checkTherapy();

                if(!check) {
                    // Query to insert the new alert message in database
                    database.insertQuery("Chat", new String[]{"Sender", "Receiver", "Text", "ReadFlag"}, new Object[]{-1, patient.getPatientID(), "Non stai seguendo in modo corretto la terapia (" + therapy.getTherapyName() + ")", 0});
                }
            }

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Take patient Data
     * @param patient
     */
    public void setPatient(Patient patient) {HomeSceneController.patient = patient;}
}
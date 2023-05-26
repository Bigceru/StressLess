package it.univr.telemedicina.controller;

import it.univr.telemedicina.TablePatientDrugs;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeSceneController implements Initializable {

    @FXML
    public Label lblPressure;
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

        // Set doctor's name/surname label, pressure label
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

    public void setPatient(Patient patient) {HomeSceneController.patient = patient;}
}

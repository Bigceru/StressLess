package it.univr.telemedicina.controller;

import it.univr.telemedicina.InfoTablePat;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserPageController implements Initializable{
    @FXML
    public Label lblPressure;
    @FXML
    public Label lblTime;
    @FXML
    public Label lblLastPressure;
    //@FXML public Label lblPressureStatus;
    @FXML
    public TableView<InfoTablePat> tableTherapies;
    @FXML
    public TableColumn<InfoTablePat, String> columnName;
    @FXML
    public TableColumn<InfoTablePat, String> columnInstruction;
    @FXML
    public TableColumn<InfoTablePat, Integer> columnAmount;
    @FXML
    public TableColumn<InfoTablePat, Integer> columnDoses;
    @FXML
    public Label lblTherapyState;

    private MainApplication newScene = new MainApplication();
    private static Patient patient;
    @FXML
    private Label lblName;
    @FXML
    private Label lblRefDoc;
    @FXML
    public Button logoutButton;



    //TABLE VIEW HOME
    private void setTable() {
        ObservableList<InfoTablePat> therapy = FXCollections.observableArrayList();
        try{
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM Therapies WHERE IDPatient = " + patient.getPatientID(),new String[]{"DrugName","DailyDoses","AmountTaken","Instructions"});
            InfoTablePat dati;

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
                dati = new InfoTablePat(info.get(i), Integer.parseInt(info.get(i+1)), Integer.parseInt(info.get(i+2)),info.get(i+3));
                therapy.add(dati);
            }
            //Setting columns
            columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
            columnDoses.setCellValueFactory(new PropertyValueFactory<>("dose"));
            columnAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            columnInstruction.setCellValueFactory(new PropertyValueFactory<>("instruction"));
            //Set items in table
            tableTherapies.setItems(therapy);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblName.setText(patient.getName());

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                lblTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
        timer.start();

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
                //lblPressureStatus.setText("Nessuna rilevazione");
            }
            else {
                lblPressure.setText(info.get(0) + "/" + info.get(1));
                lblLastPressure.setText(info.get(2));
                //lblPressureStatus.setText(checkPressure(Integer.parseInt(info.get(0)),Integer.parseInt(info.get(1))));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setTable();

    }

    public void setPatient(Patient patient){
        UserPageController.patient = patient;
    }

    /**
     * Method to handle patient logOut
     * @param actionEvent
     * @throws IOException
     */
    public void handleLogOutButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("Login.fxml", "Login Page", actionEvent);
    }

    private String checkPressure(int syntolic, int diastolic){
        ArrayList<String> category = new ArrayList<>(Arrays.asList("Ottimale","Normale","Normale - alta","Ipertensione di Grado 1 borderline","Ipertensione di Grado 1 lieve","Ipertensione di Grado 2 moderata", "Ipertensione di Grado 3 grave", "Ipertensione sistolica isolata borderline", "Ipertensione sistolica isolata"));
        ArrayList<Integer> valuesSyntolic = new ArrayList<>(Arrays.asList(120,130,139,149,159,179,180));//140-149,>=150
        ArrayList<Integer> valuesDiastolic = new ArrayList<>(Arrays.asList(80,85,89,94,99,109,110)); //<90
        int index = -1;

        //Check syntolic
        for(Integer value : valuesSyntolic){
            if(value >= syntolic){
                index = valuesSyntolic.indexOf(value);
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
                return  category.get(i);
            }
        }

        //Example syntolic = 300
        return "Valori fuori norma";
    }
}
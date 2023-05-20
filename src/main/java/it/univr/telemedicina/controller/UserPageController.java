package it.univr.telemedicina.controller;

import it.univr.telemedicina.InfoTablePat;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
    @FXML
    public Button buttonHome;
    @FXML
    public Button buttonEditPressure;
    @FXML
    public Button buttonEditProfile;
    @FXML
    public AnchorPane homeScene;
    @FXML
    public AnchorPane pressureScene;
    @FXML
    public AnchorPane editProfileScene;

    // Pressure/Drug tab variable
    @FXML
    public TextField txtPresSystolic;
    @FXML
    public TextField txtPresDiastolic;
    @FXML
    public ComboBox boxDrugs;
    @FXML
    public CheckComboBox boxSymptoms;
    @FXML
    public DatePicker datePres;
    @FXML
    public TextField txtOtherSymptoms;
    @FXML
    public ComboBox boxDrugsAmount;
    @FXML
    public DatePicker dateDrugs;
    @FXML
    public ComboBox boxTimeDrugs;

    private ObservableList<String> drugsList = FXCollections.observableArrayList(); //Drugs selected by Patient
    private ObservableList<String> sympotomsList = FXCollections.observableArrayList();

    private MainApplication newScene = new MainApplication();
    private static Patient patient;
    @FXML
    private Label lblName;
    @FXML
    private Label lblRefDoc;
    @FXML
    public Button logoutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set scene to be visible
        homeScene.setVisible(true);
        pressureScene.setVisible(false);
        editProfileScene.setVisible(false);

        // Edit button style to show the clicked one
        buttonHome.setStyle("-fx-background-color: #2A7878");

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

        // Initialize Pressure Page
        displayDrugs();
        displaySymptoms();
    }

    //TABLE VIEW HOME
    private void setTable() {
        ObservableList<InfoTablePat> therapy = FXCollections.observableArrayList();
        columnName.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/4
        columnDoses.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/5
        columnAmount.prefWidthProperty().bind(tableTherapies.widthProperty().divide(6)); // w * 1/5
        columnInstruction.prefWidthProperty().bind(tableTherapies.widthProperty().divide(3)); // w * 1/3

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
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // tableTherapies.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Method that handle button click and use the type of the button chosen to change scene
     * @param event
     * @throws IOException
     */
    public void handleChangeScene(ActionEvent event) throws IOException {
        if (event.getSource() == buttonHome) {  // Home button click
            homeScene.setVisible(true);
            pressureScene.setVisible(false);
            editProfileScene.setVisible(false);

            // Edit button style to show the clicked one
            buttonHome.setStyle("-fx-background-color: #2A7878");
            buttonEditPressure.setStyle("-fx-background-color: #0000");
            buttonEditProfile.setStyle("-fx-background-color: #0000");

        }
        else if(event.getSource() == buttonEditPressure){   // Pressure button click
            homeScene.setVisible(false);
            pressureScene.setVisible(true);
            editProfileScene.setVisible(false);

            // Edit button style to show the clicked one
            buttonHome.setStyle("-fx-background-color: #0000");
            buttonEditPressure.setStyle("-fx-background-color: #2A7878");
            buttonEditProfile.setStyle("-fx-background-color: #0000");
        }
        else if(event.getSource() == logoutButton){     // LogOut button click
            newScene.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        }
        else {      // Edit profile button click
            homeScene.setVisible(false);
            pressureScene.setVisible(false);
            editProfileScene.setVisible(true);

            // Edit button style to show the clicked one
            buttonHome.setStyle("-fx-background-color: #0000");
            buttonEditPressure.setStyle("-fx-background-color: #0000");
            buttonEditProfile.setStyle("-fx-background-color: #2A7878");
        }
    }

    public void setPatient(Patient patient){
        UserPageController.patient = patient;
    }


    // ##### Methods for Pressure page #####

    public void sendPressuresButton(ActionEvent actionEvent) {
        int systolic;
        int diastolic;
        String otherSymptoms = null;
        LocalDate pressureDate;

        try {
            Database db = new Database(2);
            systolic = Integer.parseInt(txtPresSystolic.getText());
            diastolic = Integer.parseInt(txtPresDiastolic.getText());
            //Check othrSymptoms
            if (txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
                otherSymptoms = txtOtherSymptoms.getText();

            } else if (txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
                throw new ParameterException();

            pressureDate = datePres.getValue();

            // check correction of values
            checkPressuresParameters(systolic, diastolic, pressureDate);
            sympotomsList = boxSymptoms.getCheckModel().getCheckedItems();  //take the symptoms

            // Convert symptomsList to String
            StringBuilder symptomString = new StringBuilder();
            sympotomsList.forEach(s -> symptomString.append(s).append(", "));
            symptomString.delete(symptomString.length()-2, symptomString.length());
            symptomString.append(", ").append(otherSymptoms);

            //Query for insert data in BloodPressures **Need to remove Hour, change in ConditionPressure type of data TEXT-->VARCHAR and control the +otherSymptoms**
            db.insertQuery("BloodPressures", new String[]{"IDPatient","Date","Hour","SystolicPressure","DiastolicPressure","Symptoms","ConditionPressure"}, new Object[]{patient.getPatientID(), pressureDate, "12:30:00", systolic, diastolic, symptomString.toString().replace(", Altro", ""), checkPressure(systolic, diastolic)});

            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente", Alert.AlertType.INFORMATION);
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //boxDrugs.onMouseDragExitedProperty().addListener();
        // add value in Db
    }

    public void sendDrugsButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);
            LocalDate date = dateDrugs.getValue();
            String hours =  String.valueOf(boxTimeDrugs.getValue());
            checkSymptomsParameters(date);
            String drug = boxDrugs.getValue().toString();
            

            db.insertQuery("TakenDrugs", new String[]{"IDPatient", "Date", "Hour", "DrugName", "Quantity"}, new Object[]{patient.getPatientID(), date, hours, drug, Integer.parseInt((String) boxDrugsAmount.getValue())});

            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente", Alert.AlertType.INFORMATION);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParameterException | NumberFormatException | NullPointerException e){
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        }
    }

    public void checkSymptomsParameters(LocalDate date){
        // check if the mensuration date is right
        if (date.isAfter(LocalDate.now()))
            throw new ParameterException();
    }

    public void checkPressuresParameters(int systolic, int diastolic, LocalDate datePress) {
        // check diastolic
        if (diastolic <= 0 || diastolic >= 150)
            throw new ParameterException();

        // check systolic
        if (systolic <= 0 || systolic >= 250)
            throw new ParameterException();

        if (systolic <= diastolic)
            throw new ParameterException();

        // check if the mensuration date is right
        if (datePress.isAfter(LocalDate.now()))
            throw new ParameterException();

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
    private void displayDrugs() {
        ArrayList<String> info; //List of all drugs
        try {
            Database db = new Database(2);
            info = db.getQuery("SELECT DrugName FROM Drugs", new String[]{"DrugName"});     // Take all drugs
            boxDrugs.getItems().addAll(info);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        boxDrugsAmount.getItems().addAll("1", "2", "3", "4", "5");
        boxTimeDrugs.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    }

    private void displaySymptoms() {
        // symptomps list
        boxSymptoms.getItems().addAll("Mal di testa", "Stordimento vertigini", "Ronzii nelle orecchie", "Perdite di sangue dal naso", "Dispnea", "Cardiopalmo", "Ansia", "Angoscia", "Arrossamento cutaneo", "Oliguria", "Altro");
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

}



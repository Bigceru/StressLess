package it.univr.telemedicina.controller.patient;

import it.univr.telemedicina.DrugsTablePat;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.models.users.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class DrugsSceneController implements Initializable {

    @FXML
    public ComboBox boxDrugs;
    @FXML
    public ComboBox boxDrugsAmount;
    @FXML
    public DatePicker dateDrugs;
    @FXML
    public ComboBox boxTimeDrugs;
    private final MainApplication newScene = new MainApplication();
    private static Patient patient;
    @FXML
    public TableView <DrugsTablePat> tableDrugs;
    @FXML
    public TableColumn <DrugsTablePat, LocalDate>  columnDataDrugsTable;
    @FXML
    public TableColumn <DrugsTablePat, String> columnNameDrugsTable;
    @FXML
    public TableColumn <DrugsTablePat, String> columnAmountDrugsTable;
    @FXML
    public TableColumn <DrugsTablePat, String > columnHourDrugsTable;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayDrugs();
        setTableDrugs();
    }

    /***
     * Setting of patient
     * @param patient patient to load
     */
    public void setPatient(Patient patient){  DrugsSceneController.patient = patient;
    }

    /***
     * show all drugs can be selected and their relative quantity
     */
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

    /***
     * sending and saving in the database of selected drugs
     * @param actionEvent the click event
     */
    public void sendDrugsButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);
            LocalDate date = dateDrugs.getValue();
            String hours =  String.valueOf(boxTimeDrugs.getValue());
            checkDrugsParameters(date);
            String drug = boxDrugs.getValue().toString();

            db.insertQuery("TakenDrugs", new String[]{"IDPatient", "Date", "Hour", "DrugName", "Quantity"}, new Object[]{patient.getPatientID(), date, hours, drug, Integer.parseInt((String) boxDrugsAmount.getValue())});

            setTableDrugs();
            
            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente\n ATTENZIONE: nel caso di errore dei valori inseriti puoi rimuoverli indicando gli stessi parametri e cliccando rimuovi", Alert.AlertType.INFORMATION);
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Valori non validi", "I valori inseriti sono già presenti", Alert.AlertType.ERROR);
            System.err.println(e);
        } catch (ParameterException | NumberFormatException | NullPointerException e){
            System.out.println("Error");
            System.err.println(e);
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        }
        setTableDrugs();
    }

    /***
     * remove selected drug in the database (if it is present)
     * @param actionEvent the click event
     */
    public void removeDrugsButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);
            LocalDate date = dateDrugs.getValue();
            String hours =  String.valueOf(boxTimeDrugs.getValue());
            checkDrugsParameters(date);
            String drug = boxDrugs.getValue().toString();

            db.deleteQuery("TakenDrugs", Map.of("IDPatient", patient.getPatientID(), "Date", date, "Hour", hours, "DrugName", drug, "Quantity", Integer.parseInt((String) boxDrugsAmount.getValue())));

            setTableDrugs();

            // Add pressure success
            newScene.showAlert("Eliminati","Valori eliminati correttamente", Alert.AlertType.INFORMATION);
        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
            System.err.println(e);
        }
        setTableDrugs();
    }

    /***
     * check if the hire date is consistent
     * @param date date
     */
    public void checkDrugsParameters(LocalDate date){
        // check if the mensuration date is right
        if (date.isAfter(LocalDate.now()))
            throw new ParameterException("Data errore");
        if(date.isEqual(LocalDate.now())){
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < Integer.parseInt((String) boxTimeDrugs.getValue())){
                throw new ParameterException("Ora errore");
            }
        }
    }

    /***
     * show and upload the table of all drugs taken with relative date, hour and quantity
     */
    private void setTableDrugs() {
        ObservableList<DrugsTablePat> collection = FXCollections.observableArrayList();

        try{
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM TakenDrugs WHERE IDPatient = " + patient.getPatientID(),new String[]{"Date", "Hour", "DrugName", "Quantity"});
            DrugsTablePat dati;

            // There is no therapy
            if(!info.isEmpty()) {
                // Initialize
                for (int i = 0; i < info.size() - 3; i = i + 4) {
                    dati = new DrugsTablePat(LocalDate.parse(info.get(i)), info.get(i + 1), info.get(i + 2), info.get(i + 3));
                    collection.add(dati);
                }
            }

            // Setting columns
            columnDataDrugsTable.setCellValueFactory(new PropertyValueFactory<>("date"));
            columnHourDrugsTable.setCellValueFactory(new PropertyValueFactory<>("hour"));
            columnNameDrugsTable.setCellValueFactory(new PropertyValueFactory<>("drugName"));
            columnAmountDrugsTable.setCellValueFactory(new PropertyValueFactory<>("amount"));

            // Set items in table
            tableDrugs.setItems(collection);
            tableDrugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }}

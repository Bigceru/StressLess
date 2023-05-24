package it.univr.telemedicina.controller;

import it.univr.telemedicina.DrugsTablePat;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.users.Patient;
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
    @FXML
    public TextField txtTakenAmount;
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

    public void setPatient(Patient patient){  DrugsSceneController.patient = patient;
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

    public void sendDrugsButton(ActionEvent actionEvent) {
        try {
            Database db = new Database(2);
            LocalDate date = dateDrugs.getValue();
            String hours =  String.valueOf(boxTimeDrugs.getValue());
            checkDrugsParameters(date);
            String drug = boxDrugs.getValue().toString();

            db.insertQuery("TakenDrugs", new String[]{"IDPatient", "Date", "Hour", "DrugName", "Quantity"}, new Object[]{patient.getPatientID(), date, hours, drug, Integer.parseInt((String) boxDrugsAmount.getValue())});

            // Add pressure success
            newScene.showAlert("Invio","Valori inviati correttamente\n ATTENZIONE: nel caso di errore dei valori inseriti puoi rimuoverli indicando gli stessi parametri e cliccando rimuovi", Alert.AlertType.INFORMATION);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (ParameterException | NumberFormatException | NullPointerException e){
            System.out.println("Error");
            System.out.println(e);
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        }
        setTableDrugs();
    }

    public void checkDrugsParameters(LocalDate date){
        // check if the mensuration date is right
        if (date.isAfter(LocalDate.now()))
            throw new ParameterException("Data errore");
        if(date.isEqual(LocalDate.now())){
            System.out.print(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < Integer.parseInt((String) boxTimeDrugs.getValue())){
                throw new ParameterException("Ora errore");
            }
        }
    }

    private void setTableDrugs() {
        ObservableList<DrugsTablePat> collection = FXCollections.observableArrayList();

        try{
            Database db = new Database(2);
            ArrayList<String> info = db.getQuery("SELECT * FROM TakenDrugs WHERE IDPatient = " + patient.getPatientID(),new String[]{"Date", "Hour", "DrugName", "Quantity"});
            DrugsTablePat dati;

            //There is no therapy
            if(info.isEmpty()) {
                tableDrugs.setPlaceholder(new Label("Nessun farmaco assunto in precedenza"));
                return;
            }

            //Initialize
            for(int i = 0; i < info.size()-3; i = i + 4){
                dati = new DrugsTablePat(LocalDate.parse(info.get(i)), info.get(i+1), info.get(i+2), info.get(i+3));
                collection.add(dati);
            }

            //Setting columns
            columnDataDrugsTable.setCellValueFactory(new PropertyValueFactory<>("date"));
            columnHourDrugsTable.setCellValueFactory(new PropertyValueFactory<>("hour"));
            columnNameDrugsTable.setCellValueFactory(new PropertyValueFactory<>("drugName"));
            columnAmountDrugsTable.setCellValueFactory(new PropertyValueFactory<>("amount"));
            //Set items in table
            tableDrugs.setItems(collection);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        tableDrugs.setItems(collection);

        tableDrugs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}

package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;


import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class PressionsPageController implements Initializable {

    @FXML
    public DatePicker dateDrugs;
    @FXML
    public ComboBox timeDrugs;
    @FXML
    private CheckComboBox boxDrugs;

    private MainApplication newScene = new MainApplication();
    @FXML
    public TextField txtPresSystolic;
    @FXML
    public TextField txtPresDiastolic;
    @FXML
    public TextField txtOtherSymptoms;
    @FXML
    public CheckComboBox boxSymptoms;
    @FXML
    public DatePicker datePres;

    private ObservableList<String> drugsList = FXCollections.observableArrayList(); //Drugs selected by Patient
    private ObservableList<String> sympotomsList = FXCollections.observableArrayList();


    public void initialize(URL location, ResourceBundle resources) {
        displayDrugs();
        displaySymptoms();


    }

    private void displayDrugs() {
        ArrayList<String> info = new ArrayList<>(); //List of all drugs
        try {
            Database db = new Database(2);
            info = db.getQuery("SELECT DrugName FROM Drugs", new String[]{"DrugName"}); //Take all drugs
            boxDrugs.getItems().addAll(info);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        drugsList = boxDrugs.getCheckModel().getCheckedItems();
        timeDrugs.getItems().addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    }

    private void displaySymptoms() {
        // symptomps list
        boxSymptoms.getItems().addAll("Mal di testa", "Stordimento vertigini", "Ronzii nelle orecchie", "Perdite di sangue dal naso", "Dispnea", "Cardiopalmo", "Ansia", "Angoscia", "Arrossamento cutaneo", "Oliguria ", "Altro");
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


    //send pression and symptoms
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
            sympotomsList = boxSymptoms.getCheckModel().getCheckedItems(); //take the symptoms

            //Query for insert data in BloodPressures **Need to remove Hour, change in ConditionPressure type of data TEXT-->VARCHAR and control the +otherSymptoms**
            db.insertQuery("BloodPressures",new String[]{"IDPatient","Date","Hour","SystolicPressure","DiastolicPressure","Symptoms","ConditionPressure"},new Object[]{6,pressureDate,"12:30:00",systolic,diastolic,sympotomsList+" "+otherSymptoms,checkPressure(systolic,diastolic)});

        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //boxDrugs.onMouseDragExitedProperty().addListener();
        // add value in Db
    }

    //Check parameters
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

    public void sendDrugsButton(ActionEvent actionEvent) {
        try{
            Database db = new Database(2);
            LocalDate date = dateDrugs.getValue();
            String hours =  String.valueOf(timeDrugs.getValue());
            checkSymptomsParameters(date);

            //db.insertQuery();
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

}


/*

    1   X   X   X   V -->LOGIN --> NON HAI SEGNALATO I FARMACI --> V V V

    V   X   X   X                                               --> LOGIN -->


 */

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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PressionsPageController implements Initializable {
    private MainApplication newScene = new MainApplication();
    @FXML
    public TextField txtPresSystolic;
    @FXML
    public TextField txtPresDiastolic;
    @FXML
    public TextField txtOtherSymptoms;
    @FXML
    private CheckComboBox boxDrugs;
    @FXML
    public CheckComboBox boxSymptoms;
    @FXML
    public DatePicker datePres;

    private ObservableList<String> drugsList = FXCollections.observableArrayList(); //Drugs selected by Patient
    private ObservableList<String> sympotomsList = FXCollections.observableArrayList();


    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> info = new ArrayList<>(); //List of all drugs
        try{
            Database db = new Database(2);
            info = db.getQuery("SELECT DrugName FROM Drugs", new String[]{"DrugName"}); //Take all drugs
            boxDrugs.getItems().addAll(info);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // symptomps list
        boxSymptoms.getItems().addAll("Mal di testa", "Stordimento vertigini", "Ronzii nelle orecchie", "Perdite di sangue dal naso","Dispnea", "Cardiopalmo", "Ansia", "Angoscia", "Arrossamento cutaneo", "Oliguria ", "Altro");

        drugsList = boxDrugs.getCheckModel().getCheckedItems();



        //listening if "Altro" else is entered
        boxSymptoms.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            if(boxSymptoms.getCheckModel().isChecked("Altro")){
                txtOtherSymptoms.setVisible(true);
            }else{
                txtOtherSymptoms.setVisible(false);
                txtOtherSymptoms.setText(null);
            }
        });
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
        int syntolic;
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

            System.out.println(otherSymptoms);
            pressureDate = datePres.getValue();
            // check correction of values
            checkParameters(syntolic,diastolic, pressureDate);
        }catch (ParameterException | NumberFormatException | NullPointerException e){
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //boxDrugs.onMouseDragExitedProperty().addListener();

        // add value in Db
    }

    //Check parameters
    public void checkParameters(int syntolic, int diastolic,LocalDate datePress){
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

    public void sendDrugsButton(ActionEvent actionEvent) {



        // add value in db

    }

}


/*

    1   X   X   X   V -->LOGIN --> NON HAI SEGNALATO I FARMACI --> V V V

    V   X   X   X                                               --> LOGIN -->


 */

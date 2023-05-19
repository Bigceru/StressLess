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



    //send pression and symptoms
    public void sendPressuresButton(ActionEvent actionEvent) {
        int syntolic;
        int diastolic;
        String otherSymptoms = null;
        LocalDate pressureDate;

        try{
            syntolic = Integer.parseInt(txtPresSystolic.getText());
            diastolic = Integer.parseInt(txtPresDiastolic.getText());

            //Check othrSymptoms
            if(txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
                otherSymptoms = txtOtherSymptoms.getText();

            }else if(txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
                throw new ParameterException();

            System.out.println(otherSymptoms);
            pressureDate = datePres.getValue();
            // check correction of values
            checkParameters(syntolic,diastolic, pressureDate);
        }catch (ParameterException | NumberFormatException | NullPointerException e){
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova",Alert.AlertType.ERROR);
        }

        //boxDrugs.onMouseDragExitedProperty().addListener();

        // add value in Db
    }

    //Check parameters
    public void checkParameters(int syntolic, int diastolic,LocalDate datePress){
        // check diastolic
        if(diastolic <= 0 || diastolic >= 150)
            throw new ParameterException();

        // check systolic
        if(syntolic <= 0 || syntolic >= 250 )
            throw new ParameterException();

        if(syntolic <= diastolic)
            throw new ParameterException();

        // check if the mensuration date is right
        if(datePress.isAfter(LocalDate.now()))
            throw new ParameterException();


    }

    public void sendDrugsButton(ActionEvent actionEvent) {



        // add value in db

    }

}

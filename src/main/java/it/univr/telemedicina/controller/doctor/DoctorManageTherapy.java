package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.Instructions;
import it.univr.telemedicina.utilities.Therapies;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DoctorManageTherapy implements Initializable {
    MainApplication newScene = new MainApplication();
    @FXML
    private ComboBox<String> boxTherapy;
    @FXML
    private ComboBox<String> boxTherapyName;
    @FXML
    private ComboBox<String> boxDrugs;
    @FXML
    private ComboBox<String> boxDailyDoses;
    @FXML
    private ComboBox<String> boxAmount;
    @FXML
    private CheckComboBox<String> checkBoxInstruction;
    @FXML
    private DatePicker dateStart;
    @FXML
    private DatePicker dateEnd;
    @FXML
    private Button buttonSend;
    @FXML
    private Button buttonDelete;

    // Manage note

    @FXML
    private TextArea txtAreaInformation;

    // Patient istance
    private static int idPatient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // take if present the old note
        try{
            Database database = new Database(2);
            ArrayList<String> resultNotePatientQuery = database.getQuery("SELECT Detail FROM PatientsDetails WHERE IDPatient = " + idPatient, new String[]{"Detail"});

            //fill the txt area
            if(!resultNotePatientQuery.isEmpty())
                txtAreaInformation.setText(resultNotePatientQuery.toString().replace("", " ").replace("]", ""));
            else
                txtAreaInformation.setText("");


            // INITIALIZE ALL COMBOBOX
            // Therapy (if i want modify one already present)
            ArrayList<String> resultTherapyQuery = database.getQuery("SELECT TherapyName FROM Therapies WHERE IDPatient = " + idPatient, new String[]{"TherapyName"});
            ArrayList<String> resultDrugsQuery = database.getQuery("SELECT DrugName FROM Drugs", new String[]{"DrugName"});
            boxTherapy.getItems().addAll(resultTherapyQuery);
            boxTherapy.getItems().add("Nuova");
            boxDrugs.getItems().addAll(resultDrugsQuery);
            // therapy name

            boxTherapyName.getItems().addAll(Therapies.returnCollection());
            //DailyDoses
            boxDailyDoses.getItems().addAll("01","02","03","04","05","06");
            //boxAmount
            boxAmount.getItems().addAll("01","02","03","04","05","06","07","08","09","10");
            //Istruction
            checkBoxInstruction.getItems().addAll(Instructions.returnCollection());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    // Method that controls what has been selected in box therapy
    // Set all visible (if not empy)
    // Set all box (if NUOVO is not selected)
    public void handleBoxTherapy(ActionEvent actionEvent) {
        try{
            Database database = new Database(2);

            //Check if there is a value in boxTherapy
            if(boxTherapy.getValue().isEmpty()) {
                newScene.showAlert("Errore", "Inserire il campo TERAPIA", Alert.AlertType.ERROR);
                return;
            }
            //Set all visible
            else{
                if(boxTherapy.getValue().equals("Nuova"))
                    boxTherapyName.setVisible(true);
                else
                    boxTherapyName.setVisible(false);

                boxDrugs.setVisible(true);
                boxDailyDoses.setVisible(true);
                boxAmount.setVisible(true);
                checkBoxInstruction.setVisible(true);
                dateStart.setVisible(true);
                dateEnd.setVisible(true);
                buttonSend.setVisible(true);
                buttonDelete.setVisible(true);
            }

            //If "Nuova" was NOT selected, modify the selected therapy. Set all box with relative dates
            if(!boxTherapy.getValue().equals("Nuova")){
                ArrayList<String> resultTherapiesQuery = database.getQuery("SELECT * FROM Therapies WHERE IDPatient = " + idPatient + " AND TherapyName = '" + boxTherapy.getValue() +"'", new String[]{"TherapyName","DrugName","DailyDoses","AmountTaken","Instructions","StartDate","EndDate"});
                boxTherapyName.setValue(resultTherapiesQuery.get(0));
                boxDrugs.setValue(resultTherapiesQuery.get(1));
                boxDailyDoses.setValue(resultTherapiesQuery.get(2));
                boxAmount.setValue(resultTherapiesQuery.get(3));
                checkBoxInstruction.getCheckModel().check(Instructions.valueOf(resultTherapiesQuery.get(4)).ordinal());
                System.out.println(Instructions.valueOf(resultTherapiesQuery.get(4)).ordinal());
                dateStart.setValue(LocalDate.parse(resultTherapiesQuery.get(5)));
                dateEnd.setValue(LocalDate.parse(resultTherapiesQuery.get(6)));
            }else{
                buttonSend.setText("Modifica");
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    /***
     * Method that add or modify therapy
     */
    private void handleManageButton(ActionEvent actionEvent){
        //Check if there is all box are not empty
        if(boxTherapy.getValue().isEmpty() && boxTherapyName.getValue().isEmpty() && boxDrugs.getValue().isEmpty() && boxAmount.getValue().isEmpty() && boxDailyDoses.getValue().isEmpty()){
            newScene.showAlert("Errore", "Inserire tutti i campi", Alert.AlertType.ERROR);
            return;
        }

        try{
            Database database = new Database(2);
            String[] keys = {"IDPatient","TherapyName","DrugName","DailyDoses","AmountTaken","Instructions","StartDate","EndDate"};
            Object[] values = {idPatient,boxTherapyName.getValue(),boxDrugs.getValue(),boxDailyDoses.getValue(),boxAmount.getValue(),checkBoxInstruction.getCheckModel().getCheckedItems().toString(),dateStart.getValue(),dateEnd.getValue()};

            //Map contain (Field : Values)
            Map<String, Object> info = new TreeMap<>();
            for(int i = 0; i < keys.length; i++){
                info.put(keys[i],values[i]);
            }

            //Add a new therapy
            if(boxTherapy.getValue().equals("Nuovo")){
                database.insertQuery("Therapies", keys, values);
            }
            else{
                database.updateQuery("Therapies", info, Map.of("IDPatient", idPatient,"TherapyName",boxTherapyName.getValue()));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Delete the selected entry in Therapies DATABASE
     * @param actionEvent
     */
    @FXML
    private void handleDeleteButton(ActionEvent actionEvent){
        // if box are not empty or box therapy is not NUOVO
        if(boxTherapy.getValue().isEmpty() && boxTherapyName.getValue().isEmpty() && boxDrugs.getValue().isEmpty() && boxAmount.getValue().isEmpty() && boxDailyDoses.getValue().isEmpty() && !boxTherapy.getValue().equals("Nuova")){
            newScene.showAlert("Errore", "Inserire tutti i campi", Alert.AlertType.ERROR);
            return;
        }

        try{
            Database database = new Database(2);
            database.deleteQuery("Therapies",Map.of("IDPatient", idPatient,"TherapyName",boxTherapyName.getValue()));
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Errore Eliminazione0", "Errore! Dati non eliminati, Riprovare", Alert.AlertType.ERROR);
            return;
        }
    }

    /**
     * Update the PatientsDetails DATABASE
     * @param actionEvent
     */
    @FXML
    private void handleSendInformationButton(ActionEvent actionEvent){

       try {
           Database database = new Database(2);
           //Check if exist
           ArrayList<String> list = database.getQuery("SELECT * FROM PatientsDetails WHERE IDPatient = " + idPatient, new String[]{"IDPatient", "Detail"});

           //If exist update the database
           if(!list.isEmpty()) {
               database.updateQuery("PatientsDetails", Map.of("Detail", txtAreaInformation.getText()), Map.of("IDPatient", idPatient));
               newScene.showAlert("Invio", "I Dati sono stati aggiornati correttamente", Alert.AlertType.INFORMATION);
           }
           else {  // add for the first time
               database.insertQuery("PatientsDetails", new String[]{"IDPatient", "Detail"}, new Object[]{idPatient, txtAreaInformation.getText()});
           }

       } catch (SQLException | ClassNotFoundException e) {  // Send a show aller if the update has failed
           newScene.showAlert("Errore Invio", "Errore! Dati non aggiornati, Riprovare", Alert.AlertType.ERROR);
           return;
       }
    }

    /**
     *method to take the id of the patient
     * @param idPatient integer
     */
    public static void setPatient(int idPatient) {
        DoctorManageTherapy.idPatient = idPatient;
    }



}

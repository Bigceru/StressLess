package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.models.Therapy;
import it.univr.telemedicina.models.TherapyList;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.Instructions;
import it.univr.telemedicina.utilities.Therapies;
import it.univr.telemedicina.utilities.TherapyFields;
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
    @FXML
    private Label lblNameTherapy;
    @FXML
    private Label lblDailyDoses;
    @FXML
    private Label lblDrugs;
    @FXML
    private Label lblAmount;
    @FXML
    private Label lblDateStart;
    @FXML
    private Label lblDateEnd;
    @FXML
    private Label lblInstruction;

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
                txtAreaInformation.setText(resultNotePatientQuery.toString().substring(1, resultNotePatientQuery.toString().length()-1));
            else
                txtAreaInformation.setText("");

            // INITIALIZE ALL COMBOBOX
            // Therapy (if i want modify one already present)
            TherapyList therapyList = new TherapyList();
            ArrayList<String> resultTherapyQuery = therapyList.getWhatUWantString(therapyList.getTherapyToString(therapyList.getTherapyById(idPatient)), new TherapyFields[]{TherapyFields.THERAPY_NAME, TherapyFields.DRUG_NAME});

            ArrayList<String> resultDrugsQuery = database.getQuery("SELECT DrugName FROM Drugs", new String[]{"DrugName"});

            // crete the therapy, merging Therapy and DrugName
            ArrayList<String> result = new ArrayList<>();
            for(int i = 0; i< resultTherapyQuery.size()-1; i+=2){
                result.add(resultTherapyQuery.get(i) + " - " + resultTherapyQuery.get(i+1));
            }

            boxTherapy.getItems().addAll(result);
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

    /**
     * Method that controls what has been selected in box therapy
     * Set all visible (if not empty)
     * Set all box (if NUOVO is not selected)
     */
    @FXML
    public void handleBoxTherapy(ActionEvent actionEvent) {

        // Check if there is a value in boxTherapy
        if(boxTherapy.getValue().isEmpty()) {
            newScene.showAlert("Errore", "Inserire il campo TERAPIA", Alert.AlertType.ERROR);
        }
        // Set all visible
        else {
            // If "Nuova" was selected
            if(boxTherapy.getValue().equals("Nuova")) {
                boxTherapyName.setVisible(true);
                lblNameTherapy.setVisible(true);
                buttonSend.setText("Aggiungi");
            }
            else {  // If "Nuova" was NOT selected, modify the selected therapy. Set all box with relative dates
                // Edit button specs
                boxTherapyName.setVisible(false);
                lblNameTherapy.setVisible(false);
                buttonSend.setText("Modifica");

                TherapyList therapyList = new TherapyList();
                // Do a query and add all the values in the fields/boxes
                Therapy resultTherapiesQuery = null; //TherapyName, drugName and id make the therapy unique

                String therapyName = boxTherapy.getValue().split("-")[0].trim();
                String drugName = boxTherapy.getValue().split("-")[1].substring(1);

                // Cycle for search based TherapyName and DrugsName
                for(Therapy therapy : therapyList) {
                    if(therapy.getTherapyName().equals(therapyName) && therapy.getDrugName().equals(drugName))
                        resultTherapiesQuery = therapy;
                }

                assert resultTherapiesQuery != null;
                boxTherapyName.setValue(resultTherapiesQuery.getTherapyName());
                boxDrugs.setValue(resultTherapiesQuery.getDrugName());
                boxDailyDoses.setValue(String.valueOf(resultTherapiesQuery.getAmountTaken()));
                boxAmount.setValue(String.valueOf(resultTherapiesQuery.getAmountTaken()));

                // Cicle all the instruction and set in which period of the day the patient have to take the pillow
                for(String instruction : resultTherapiesQuery.getInstructions().split(","))
                    checkBoxInstruction.getCheckModel().check(Instructions.valueOf(instruction.trim().replace(" ", "_")).ordinal());

                dateStart.setValue(LocalDate.parse(resultTherapiesQuery.getStartDate().toString()));
                dateEnd.setValue(LocalDate.parse(resultTherapiesQuery.getEndDate().toString()));
            }

            // Set label visibility properties
            boxDrugs.setVisible(true);
            lblAmount.setVisible(true);
            lblDailyDoses.setVisible(true);
            lblDrugs.setVisible(true);
            lblDateEnd.setVisible(true);
            lblDateStart.setVisible(true);
            lblInstruction.setVisible(true);
            boxDailyDoses.setVisible(true);
            boxAmount.setVisible(true);
            checkBoxInstruction.setVisible(true);
            dateStart.setVisible(true);
            dateEnd.setVisible(true);
            buttonSend.setVisible(true);
            buttonDelete.setVisible(true);
        }
    }

    /**
     * Method that add or modify therapy
     */
    @FXML
    private void handleManageButton(ActionEvent actionEvent){
        //checkCombo return false, send error
        if(!checkCombo()){
            return;
        }

        try {
            String instruction = checkBoxInstruction.getCheckModel().getCheckedItems().toString();
            Therapy therapy = new Therapy(idPatient, boxTherapyName.getValue(), boxDrugs.getValue(), Integer.parseInt(boxDailyDoses.getValue()), Integer.parseInt(boxAmount.getValue()), instruction.substring(1, instruction.length()-1), dateStart.getValue(), dateEnd.getValue());
            
            // Add a new therapy
            if(boxTherapy.getValue().equals("Nuova")){
                therapy.insertInDatabase();
                // database.insertQuery("Therapies", keys, values);
                newScene.showAlert("Invio", "Dati aggiunti correttamente", Alert.AlertType.INFORMATION);
            }
            else {
                //database.updateQuery("Therapies", info, Map.of("IDPatient", idPatient,"TherapyName",boxTherapyName.getValue()));
                therapy.updateInDatabase(Map.of("IDPatient", idPatient,"TherapyName",boxTherapyName.getValue()));
                newScene.showAlert("Invio", "Dati modificati correttamente", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Errore inserimento/aggiornamento", "Errore! Dati non aggiornati/inseriti, Riprovare", Alert.AlertType.ERROR);
        }
    }

    /**
     * Method to delete the selected entry in Therapies DATABASE when pressed delete button
     */
    @FXML
    private void handleDeleteButton(ActionEvent actionEvent){
        //checkCombo return false, send error
        if(!checkCombo()){
           return;
        }

        try{
            //database.deleteQuery("Therapies",Map.of("IDPatient", idPatient,"TherapyName",boxTherapyName.getValue(),"DrugName",boxDrugs.getValue()));
            // Create the therapy instance and then remove it from the db
            Therapy therapy = new Therapy(idPatient, boxTherapyName.getValue(), boxDrugs.getValue(), Integer.parseInt(boxDailyDoses.getValue()), Integer.parseInt(boxAmount.getValue()), null, dateStart.getValue(), dateEnd.getValue());
            therapy.removeInDatabase();
            newScene.showAlert("Elimina", "Dati  elimanti correttamente", Alert.AlertType.INFORMATION);
        } catch (SQLException | ClassNotFoundException e) {
            newScene.showAlert("Errore Eliminazione", "Errore! Dati non eliminati, Riprovare", Alert.AlertType.ERROR);
        }
    }

    /**
     * Method to update the PatientsDetails DATABASE when pressed send information button
     */
    @FXML
    private void handleSendInformationButton(ActionEvent actionEvent){
        //checkCombo return false, send error
        if(!checkCombo()){
            return;
        }

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
       }
    }

    /**
     * Method to take the id of the patient
     * @param idPatient integer
     */
    public static void setPatient(int idPatient) {
        DoctorManageTherapy.idPatient = idPatient;
    }

    // Check all box if are empty
    // Return false if there is a problem with controls
    public boolean checkCombo(){
        // if box are not empty or box therapy is not NUOVO
        if(boxTherapy.getValue().isEmpty() && boxTherapyName.getValue().isEmpty() && boxDrugs.getValue().isEmpty() && boxAmount.getValue().isEmpty() && boxDailyDoses.getValue().isEmpty() && !boxTherapy.getValue().equals("Nuova")){
            newScene.showAlert("Errore", "Inserire tutti i campi", Alert.AlertType.ERROR);
            return false;
        }

        // Check dates (endDate must be at least one day after startDate)
        if(dateEnd.getValue() == null || dateStart.getValue() == null || dateEnd.getValue().isBefore(dateStart.getValue().plusDays(1)) || (boxTherapy.getValue().equals("Nuova") && dateStart.getValue().isBefore(LocalDate.now()))){
            newScene.showAlert("Errore", "Date inserite non consistenti", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
}

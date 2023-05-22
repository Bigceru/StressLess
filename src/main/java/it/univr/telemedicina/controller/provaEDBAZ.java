package it.univr.telemedicina.controller;

import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.utilities.Database;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
public class provaEDBAZ {
    try {
        Database db = new Database(2);
        systolic = Integer.parseInt(txtPresSystolic.getText());
        diastolic = Integer.parseInt(txtPresDiastolic.getText());

        //Check otherSymptoms
        if (txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
            otherSymptoms = txtOtherSymptoms.getText();

        } else if (txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
            throw new ParameterException("Campo di testo altri sintomi error");

        pressureDate = datePres.getValue();

        // check correction of values
        checkPressuresParameters(systolic, diastolic, pressureDate);

        ObservableList<String> symptomsList = boxSymptoms.getCheckModel().getCheckedItems();  //take the symptoms

        // Convert symptomsList to String
        StringBuilder symptomString;
        if (symptomsList.isEmpty())
            symptomString = new StringBuilder();
        else {
            symptomString = new StringBuilder();
            symptomsList.forEach(s -> symptomString.append(s).append(", "));
            symptomString.delete(symptomString.length() - 2, symptomString.length());
            symptomString.append(", ").append(otherSymptoms);
        }

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

    public void removePressuresButton(ActionEvent actionEvent) {
        int systolic;
        int diastolic;
        LocalDate pressureDate;
        String otherSymptoms = null;
        try {
            Database db = new Database(2);
            Map<String, Object> dati = new TreeMap<>();
            systolic = Integer.parseInt(txtPresSystolic.getText());
            diastolic = Integer.parseInt(txtPresDiastolic.getText());
            //Check otherSymptoms
            if (txtOtherSymptoms.isVisible() && !txtOtherSymptoms.getText().isEmpty()) {
                otherSymptoms = txtOtherSymptoms.getText();

            } else if (txtOtherSymptoms.isVisible() && txtOtherSymptoms.getText().isEmpty())
                throw new ParameterException("Campo di testo altri sintomi error");
            pressureDate = datePres.getValue();
            // check correction of values
            checkPressuresParameters(systolic, diastolic, pressureDate);
            ObservableList<String> symptomsList = boxSymptoms.getCheckModel().getCheckedItems();  //take the symptoms
            StringBuilder symptomString;
            if (symptomsList.isEmpty())
                symptomString = new StringBuilder();
            else {
                symptomString = new StringBuilder();
                symptomsList.forEach(s -> symptomString.append(s).append(", "));
                symptomString.delete(symptomString.length() - 2, symptomString.length());
                symptomString.append(", ").append(otherSymptoms);
            }
            List<Object> values = Arrays.asList(systolic,diastolic,symptomsList,pressureDate);
            dati.put("SystolicPressure",systolic);
            dati.put("DiastolicPressure",diastolic);
            dati.put("Date",datePres);
            dati.put("Symptoms",symptomsList);
            db.deleteQuery("BloodPressures",dati);

        } catch (ParameterException | NumberFormatException | NullPointerException e) {
            System.out.println("Error");
            newScene.showAlert("Valori non validi", "Valori inseriti non validi, riprova", Alert.AlertType.ERROR);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
}

*/

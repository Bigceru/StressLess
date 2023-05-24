package it.univr.telemedicina.controller.doctor;


import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import it.univr.telemedicina.controller.EditProfileSceneController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;


public class ChangePasswordController{

    MainApplication newScene = new MainApplication();
    private static Doctor doctor;
    @FXML
    public PasswordField txtPassword;
    @FXML
    public Button buttonSaveNewPass;
    @FXML
    public PasswordField txtConfirmPassword;
    EditProfileSceneController doctorNewData = new EditProfileSceneController();

    public void saveNewPassword(ActionEvent action){
        Map<String, Object> dati = new TreeMap<>();
        boolean canUpdate = false;
        if(!txtPassword.getText().isEmpty() && !txtConfirmPassword.getText().isEmpty() && doctor.checkPassword(txtPassword.getText()) && doctorNewData.equalPassword(txtPassword.getText(), txtConfirmPassword.getText())){
            dati.put("Password", txtPassword.getText());
            canUpdate = true;
        }
        else{
            txtConfirmPassword.setStyle("-fx-text-fill: red;");
            txtPassword.setStyle("-fx-text-fill: red;");
        }

        if(canUpdate){
            try {
                Database db = new Database(2);
                db.updateQuery("Doctors", dati, Map.of("ID", doctor.getID()));

                doctor.setPassword(dati.get("Password").toString());

                newScene.showAlert("Dati", "Aggiornati con succeso!", Alert.AlertType.INFORMATION);
                newScene.closeScene("/it/univr/telemedicina/doctorPages/ChangePasswordScene.fxml", action);
            } catch (SQLException | ClassNotFoundException e) {
                newScene.showAlert("Error", "Errore caricamento", Alert.AlertType.ERROR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            newScene.showAlert("Dati", "Errore aggiornamento dati", Alert.AlertType.ERROR);
    }

    public void setDoctor(Doctor doctor) {
        ChangePasswordController.doctor = doctor;
    }


}

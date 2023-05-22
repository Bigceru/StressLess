package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.beans.binding.MapExpression;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class LoginController implements Initializable {
    private final MainApplication newScene = new MainApplication();
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private ComboBox comboUserBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboUserBox.getItems().add("Patient");
        comboUserBox.getItems().add("Doctor");
    }

    public void handleLoginButton(ActionEvent actionEvent) throws IOException {
        ArrayList<String> credentials;

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Change page if login has success
        if(username.isEmpty() || password.isEmpty()){
            newScene.showAlert("Campi non pieni","Compila tutti i campi", Alert.AlertType.ERROR);
        }
        else {
            try {
                String tableName;
                if(comboUserBox.getValue().equals("Doctor"))   // Check if I want to login as doctor or patient
                    tableName = "Doctors";
                else
                    tableName = "Patients";

                // connect to the database
                Database database = new Database(2);
                credentials = database.getQuery("SELECT * FROM " + tableName + " WHERE username = " + "\"" + txtUsername.getText() + "\" AND password = " + "\"" + txtPassword.getText() + "\"", new String[]{"name", "surname", "email", "phoneNumber", "username", "password", "birthPlace", "province", "birthDate", "domicile", "sex", "taxIDCode", "refDoc"});
                database.closeAll();

                // if is empty run error
                if (credentials.isEmpty())
                    newScene.showAlert("Error with credentials","Error with credentials", Alert.AlertType.ERROR);
                else {
                    if(tableName.equals("Patients")) {   // Check where I have to go next
                        // Create new patient with the last query
                        Patient patient = new Patient(null, credentials.get(0), credentials.get(1), credentials.get(2),credentials.get(3),credentials.get(4),credentials.get(5),credentials.get(6),credentials.get(7), LocalDate.parse(credentials.get(8)), credentials.get(9), credentials.get(10).charAt(0),credentials.get(11),Integer.parseInt(credentials.get(12)));

                        // Attach the new patient to the controller
                        UserPageController controller = new UserPageController();
                        controller.setPatient(patient);
                        // Passing patient
                        PressureSceneController pressureController = new PressureSceneController();
                        pressureController.setPatient(patient);

                        // change scene
                        newScene.changeScene("UserPage.fxml","title", actionEvent);
                    }
                    else
                        // change scene
                        System.out.println("Vado nella pagina dei dottori");
                }
            } catch (SQLException e) {
                System.out.println("Error with login");

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleRegisterButton(ActionEvent actionEvent) throws IOException {
        if(comboUserBox.getValue().equals("Patient"))
            newScene.changeScene("Registration.fxml", "Registrazione utente", actionEvent);
        else
            System.out.println("Non sei un paziente");
    }

    public void handleComboBoxChoose(ActionEvent actionEvent) {
        if (comboUserBox.getValue() == "Patient") {
            loginButton.setVisible(true);
            registerButton.setVisible(true);
        } else if (comboUserBox.getValue() == "Doctor") {
            loginButton.setVisible(true);
            registerButton.setVisible(false);
        } else {
            loginButton.setVisible(false);
            registerButton.setVisible(false);
        }
    }
}

package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.controller.doctor.ChangePasswordController;
import it.univr.telemedicina.controller.doctor.DoctorHomeSceneController;
import it.univr.telemedicina.controller.doctor.DoctorPageController;
import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Class to control Login page
 */
public class LoginController implements Initializable {
    private final MainApplication newScene = new MainApplication();
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;
    @FXML
    private ComboBox<String> comboUserBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboUserBox.getItems().add("Patient");
        comboUserBox.getItems().add("Doctor");
    }

    /**
     * Method to handle loginButton action
     *
     */
    public void handleLoginButton(ActionEvent actionEvent) throws IOException {
        ArrayList<String> credentials;  // ArrayList to contains credentials values

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // If username or password fields are empty
        if(username.isEmpty() || password.isEmpty()){
            newScene.showAlert("Campi non pieni","Compila tutti i campi", Alert.AlertType.ERROR);   // Show alert message
        }
        else {
            try {
                String tableName;

                // Check if I want to login as doctor or patient
                if(comboUserBox.getValue().equals("Doctor"))
                    tableName = "Doctors";
                else
                    tableName = "Patients";

                // Connect to the database
                Database database = new Database(2);

                // If the user type is patient
                if(tableName.equals("Patients"))
                    credentials = database.getQuery("SELECT * FROM " + tableName + " WHERE username = " + "\"" + txtUsername.getText() + "\" AND password = " + "\"" + txtPassword.getText() + "\"", new String[]{"name", "surname", "email", "phoneNumber", "username", "password", "birthPlace", "province", "birthDate", "domicile", "sex", "taxIDCode", "refDoc"});
                else    // If is doctor
                    credentials = database.getQuery("SELECT * FROM " + tableName + " WHERE username = " + "\"" + txtUsername.getText() + "\" AND password = " + "\"" + txtPassword.getText() + "\"", new String[]{"name", "surname", "email", "phoneNumber", "username", "password"});

                database.closeAll();

                // if is empty run error
                if (credentials.isEmpty())
                    newScene.showAlert("Error with credentials","Error with credentials", Alert.AlertType.ERROR);
                else {
                    if(tableName.equals("Patients")) {   // Check where I have to go next
                        // Create new patient with the last query
                        Patient patient = new Patient(null, credentials.get(0), credentials.get(1), credentials.get(2),credentials.get(3),credentials.get(4),credentials.get(5),credentials.get(6),credentials.get(7), LocalDate.parse(credentials.get(8)), credentials.get(9), credentials.get(10).charAt(0),credentials.get(11),Integer.parseInt(credentials.get(12)));

                        HomeSceneController homeController = new HomeSceneController();
                        homeController.setPatient(patient);
                        // Attach the new patient to the controller
                        UserPageController controller = new UserPageController();
                        controller.setPatient(patient);
                        // Passing patient
                        PressureSceneController pressureController = new PressureSceneController();
                        pressureController.setPatient(patient);

                        DrugsSceneController drugsController = new DrugsSceneController();
                        drugsController.setPatient(patient);

                        EditProfileSceneController editProfileController = new EditProfileSceneController();
                        editProfileController.setUser(patient);

                        // change scene
                        newScene.changeScene("UserPage.fxml","Paziente", actionEvent);
                    }
                    else {
                        // change scene
                        // Create new Doctor with the last query
                        Doctor doctor = new Doctor(credentials.get(0), credentials.get(1), credentials.get(2),credentials.get(3),credentials.get(4),credentials.get(5));

                        DoctorPageController pageController = new DoctorPageController();
                        pageController.setDoctor(doctor);
                        DoctorHomeSceneController homeDoctor = new DoctorHomeSceneController();
                        homeDoctor.setDoctor(doctor);
                        newScene.changeScene("doctorPages/DoctorPage.fxml", "Dottore", actionEvent);

                        if(credentials.get(5).equals("1234")){
                            ChangePasswordController changePassword = new ChangePasswordController();
                            changePassword.setDoctor(doctor);
                            newScene.addScene("/it/univr/telemedicina/doctorPages/ChangePasswordScene.fxml");
                        }
                    }
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
        if (Objects.equals(comboUserBox.getValue(), "Patient")) {
            loginButton.setVisible(true);
            registerButton.setVisible(true);
        } else if (Objects.equals(comboUserBox.getValue(), "Doctor")) {
            loginButton.setVisible(true);
            registerButton.setVisible(false);
        } else {
            loginButton.setVisible(false);
            registerButton.setVisible(false);
        }
    }
}

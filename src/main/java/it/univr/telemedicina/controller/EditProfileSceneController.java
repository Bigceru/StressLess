package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class EditProfileSceneController implements Initializable {
    MainApplication newScene = new MainApplication();
    @FXML
    public TextField txtName;
    @FXML
    public PasswordField txtPassword;
    @FXML
    public TextField txtSurname;
    @FXML
    public TextField txtBirthDate;
    @FXML
    public TextField txtProvince;
    @FXML
    public TextField txtUsername;
    @FXML
    public TextField txtDomicile;
    @FXML
    public TextField txtPhoneNumber;
    @FXML
    public TextField txtEmail;
    @FXML
    public TextField txtBirthPlace;
    @FXML
    public TextField txtTaxIDCode;
    @FXML
    public TextField txtConfirmPassword;
    @FXML
    public Button buttonSave;


    private boolean flag = true;
    private static Patient patient;

    public void initialize(URL location, ResourceBundle resources) {
        txtName.setText(patient.getName());
        txtSurname.setText(patient.getSurname());
        txtTaxIDCode.setText(patient.getTaxIDCode());
        txtBirthDate.setText(String.valueOf(patient.getBirthDate()));
        txtBirthPlace.setText(patient.getBirthPlace());
        txtProvince.setText(patient.getProvince());
        txtUsername.setText(patient.getUsername());
        txtDomicile.setText(patient.getDomicile());
        txtEmail.setText(patient.getEmail());
        txtPassword.setText(patient.getPassword());
        txtPhoneNumber.setText(patient.getPhoneNumber());
    }
    public void buttonSaveOnAction(ActionEvent actionEvent) {
        Map<String, Object> dati = new TreeMap<>();

        // reset color
        txtUsername.setStyle("-fx-text-fill: black;");
        txtPassword.setStyle("-fx-text-fill: black;");
        txtConfirmPassword.setStyle("-fx-text-fill: black;");
        txtDomicile.setStyle("-fx-text-fill: black;");
        txtUsername.setStyle("-fx-text-fill: black;");
        txtPhoneNumber.setStyle("-fx-text-fill: black;");
        txtEmail.setStyle("-fx-text-fill: black;");
        flag = true;

        // all field empty -> nothing change
        if(txtUsername.getText().isEmpty() && txtPassword.getText().isEmpty() && txtConfirmPassword.getText().isEmpty() && txtDomicile.getText().isEmpty() && txtPhoneNumber.getText().isEmpty() && txtEmail.getText().isEmpty())
            newScene.showAlert("DATI","Nessun campo scritto", Alert.AlertType.INFORMATION);

        //if all checks are passed, update in database
        if(checkUsername(txtUsername.getText()))
            dati.put("Username", txtUsername.getText());

        if(checkEmail(txtEmail.getText()))
            dati.put("Email", txtEmail.getText());

        if(checkPhoneNumber(txtPhoneNumber.getText()))
            dati.put("phoneNumber", txtPhoneNumber.getText());

        if(!txtDomicile.getText().isEmpty())
            dati.put("domicile", txtDomicile.getText());

        if(checkPassword(txtPassword.getText()) && equalPassword(txtPassword.getText(),txtConfirmPassword.getText()))
            dati.put("Password", txtPassword.getText());

        // update database
        if(!dati.isEmpty() && flag) {
            try {
                Database db = new Database(2);
                db.updateQuery("Patients", dati, Map.of("ID", ((Object) patient.getPatientID())));

                if(dati.containsKey("Username"))
                    patient.setUsername(dati.get("Username").toString());
                if(dati.containsKey("Password"))
                    patient.setPassword(dati.get("Password").toString());
                if(dati.containsKey("phoneNumber"))
                    patient.setPhoneNumber(dati.get("phoneNumber").toString());
                if(dati.containsKey("domicile"))
                    patient.setDomicile(dati.get("domicile").toString());
                if(dati.containsKey("email"))
                    patient.setEmail(dati.get("email").toString());

                System.out.println(patient.toString());
                newScene.showAlert("Dati", "Aggiornati con succeso!", Alert.AlertType.INFORMATION);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // already exist username
    protected boolean checkUsername(String username) {
        // looking for Username already exist or is empty
        if(!username.isEmpty())
            if (!patient.alreadyExist("Patients", "username", username))
                return true;
            else {
                flag = false;
                txtUsername.setStyle("-fx-text-fill: red;");
            }
        return false;
    }

    // Password and confirm password pass the validation
    protected boolean checkPassword(String password) {
        if(!password.isEmpty() )
            if(password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.\\-_:,;])(?=\\S+$).{8,}$"))
                return true;
            else {
                flag = false;
                txtPassword.setStyle("-fx-text-fill: red;");
            }
        return false;
    }

    // check lenght Phonenumber
    protected  boolean checkPhoneNumber(String phoneNumber){
        if(!phoneNumber.isEmpty())
            if(phoneNumber.matches("^[0-9]{10,15}$") && !patient.alreadyExist("Patients", "phoneNumber", phoneNumber))
                return true;
            else{
                flag = false;
                txtPhoneNumber.setStyle("-fx-text-fill: red;");}
        return false;
    }

    // check if email contain @ , .
    protected boolean checkEmail(String email){
        if(!email.isEmpty())
            if(email.contains("@") && email.contains(".") && !patient.alreadyExist("Patients", "email", email))
                return true;
            else{
                flag = false;
                txtEmail.setStyle("-fx-text-fill: red;");}
        return false;
    }

    //check if password and confirm password are equal
    protected boolean equalPassword(String password, String confirmPassword){
        if(password.equals(confirmPassword))
            return true;
        else{
            flag = false;
            txtPassword.setStyle("-fx-text-fill: red;");
            txtConfirmPassword.setStyle("-fx-text-fill: red;");
        }
        return false;
    }

    public void setPatient(Patient patient) {
        EditProfileSceneController.patient = patient;
    }
}

/**********************************
 DA RICORDARE
 ***********************************
 *
 * Posso mettere public already exist?
 */
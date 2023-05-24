package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.users.User;
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
    public PasswordField txtConfirmPassword;
    @FXML
    public Button buttonSave;


    private static User user;

    public void initialize(URL location, ResourceBundle resources) {
        txtName.setText(user.getName());
        txtSurname.setText(user.getSurname());
        txtUsername.setText(user.getUsername());
        txtEmail.setText(user.getEmail());
        // txtPassword.setText(user.getPassword());
        txtPhoneNumber.setText(user.getPhoneNumber());

        // For Patient
        if(user instanceof Patient) {
            txtProvince.setText(((Patient) user).getProvince());
            txtTaxIDCode.setText(((Patient) user).getTaxIDCode());
            txtBirthDate.setText(String.valueOf((((Patient) user).getBirthDate())));
            txtBirthPlace.setText(((Patient) user).getBirthPlace());
            txtDomicile.setText(((Patient) user).getDomicile());
        }
    }

    public void buttonSaveOnAction(ActionEvent actionEvent) {
        Map<String, Object> dati = new TreeMap<>();
        boolean canUpload = true;

        // reset color
        txtUsername.setStyle("-fx-text-fill: black;");
        txtPassword.setStyle("-fx-text-fill: black;");
        txtConfirmPassword.setStyle("-fx-text-fill: black;");
        txtDomicile.setStyle("-fx-text-fill: black;");
        txtUsername.setStyle("-fx-text-fill: black;");
        txtPhoneNumber.setStyle("-fx-text-fill: black;");
        txtEmail.setStyle("-fx-text-fill: black;");

        // all field empty -> nothing change
        if (txtUsername.getText().isEmpty() && txtPassword.getText().isEmpty() && txtConfirmPassword.getText().isEmpty() && txtDomicile.getText().isEmpty() && txtPhoneNumber.getText().isEmpty() && txtEmail.getText().isEmpty())
            newScene.showAlert("DATI", "Nessun campo scritto", Alert.AlertType.INFORMATION);

        //if all checks are passed, update in database
        if (user.checkUsername(txtUsername.getText()))
            dati.put("Username", txtUsername.getText());
        else {
            txtUsername.setStyle("-fx-text-fill: red;");
            canUpload = false;
        }

        if (user.checkEmail(txtEmail.getText()))
            dati.put("Email", txtEmail.getText());
        else {
            canUpload = false;
            txtEmail.setStyle("-fx-text-fill: red;");
        }

        if (user.checkPhoneNumber(txtPhoneNumber.getText()))
            dati.put("phoneNumber", txtPhoneNumber.getText());
        else {
            canUpload = false;
            txtPhoneNumber.setStyle("-fx-text-fill: red;");
        }

        if (!txtDomicile.getText().isEmpty())
            dati.put("domicile", txtDomicile.getText());
        else {
            canUpload = false;
            txtDomicile.setStyle("-fx-text-fill: red;");
        }

        if(user.checkPassword(txtPassword.getText()) && equalPassword(txtPassword.getText(),txtConfirmPassword.getText()))
            dati.put("Password", txtPassword.getText());
        else if(!txtPassword.getText().isEmpty()){
            txtPassword.setStyle("-fx-text-fill: red;");
            txtConfirmPassword.setStyle("-fx-text-fill: red;");
            canUpload = false;
        }

        // update database
        if(!dati.isEmpty() && canUpload) {
            try {
                Database db = new Database(2);

                if(user instanceof Patient)
                    db.updateQuery("Patients", dati, Map.of("ID", (((Patient) user).getPatientID())));
                else
                    db.updateQuery("Doctors", dati, Map.of("ID", (((Doctor) user).getID())));

                if(dati.containsKey("Username"))
                    user.setUsername(dati.get("Username").toString());
                if(dati.containsKey("Password"))
                    user.setPassword(dati.get("Password").toString());
                if(dati.containsKey("phoneNumber"))
                    user.setPhoneNumber(dati.get("phoneNumber").toString());
                if(dati.containsKey("domicile") && user instanceof Patient)
                    ((Patient) user).setDomicile(dati.get("domicile").toString());
                if(dati.containsKey("email"))
                    user.setEmail(dati.get("email").toString());

                System.out.println(user.toString());
                newScene.showAlert("Dati", "Aggiornati con succeso!", Alert.AlertType.INFORMATION);
            } catch (SQLException | ClassNotFoundException e) {
                newScene.showAlert("Error", "Errore caricamento", Alert.AlertType.ERROR);
            }
        }
        else
            newScene.showAlert("Dati", "Errore aggiornamento dati", Alert.AlertType.ERROR);
    }

    //check if password and confirm password are equal (Usato per il pop-up del primo login doctor)
    public boolean equalPassword(String password, String confirmPassword){
        if(password.equals(confirmPassword))
            return true;
        else
            return false;
    }

    public void setUser(User user) {
        EditProfileSceneController.user = user;
    }
}

/**********************************
 DA RICORDARE
 ***********************************
 *
 * Posso mettere public already exist?
 */
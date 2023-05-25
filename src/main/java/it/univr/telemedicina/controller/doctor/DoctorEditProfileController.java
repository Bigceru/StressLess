package it.univr.telemedicina.controller.doctor;

import it.univr.telemedicina.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DoctorEditProfileController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class RegistrationController implements Initializable {
    private final MainApplication newScene = new MainApplication();
    private ToggleGroup sexChooserGroup;
    @FXML
    private ComboBox<String> comboBoxRefDoc;
    @FXML
    private Label lblRefDoc;
    @FXML
    private ComboBox<String> comboBoxProvince;
    @FXML
    private Label lblProvince;
    @FXML
    private RadioButton maleButton;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private Label lblSex;
    @FXML
    private TextField txtName;
    @FXML
    private Label lblName;
    @FXML
    private TextField txtSurname;
    @FXML
    private Label lblSurname;
    @FXML
    private TextField txtBirthPlace;
    @FXML
    private Label lblBirthPlace;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDomicile;
    @FXML
    private Label lblDomicile;
    @FXML
    private TextField txtTaxIDCode;
    @FXML
    private Label lblTaxIDCode;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;
    @FXML
    private Label lblPassword;
    @FXML
    private Label wrongRegistration;
    @FXML
    private DatePicker txtBirthDate;
    @FXML
    private Label lblBirthDate;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPhoneNumber;
    private static ArrayList<Integer> doctorID = new ArrayList<>();
    @FXML
    private TextArea errorCodeLabel;
    private StringBuilder errorString = new StringBuilder();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // initialize Province ComboBox
            Database database1 = new Database(1);
            ArrayList<String> provinces = database1.getQuery("SELECT Sigla FROM Provincie", new String[]{"Sigla"});   // Query for provinces

            comboBoxProvince.getItems().addAll(provinces);
            comboBoxProvince.setVisibleRowCount(5);

            // initialize Doctor ComboBox
            Database database2 = new Database(2);
            ArrayList<String> docNS = database2.getQuery("SELECT ID, name, surname FROM Doctors", new String[]{"ID", "name", "surname"}); // Query for doctors names
            // create the doctor name
            ArrayList<String> doctorNames = new ArrayList<>();

            for(int i = 0; i < docNS.size()-1; i= i+3) {
                doctorID.add(Integer.parseInt(docNS.get(i)));
                doctorNames.add(docNS.get(i+1) + " " + docNS.get(i + 2));
            }

            comboBoxRefDoc.getItems().addAll(doctorNames);
            comboBoxRefDoc.setVisibleRowCount(5);
            database1.closeAll();
            database2.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Set RadioButton Group
        sexChooserGroup = new ToggleGroup();
        maleButton.setToggleGroup(sexChooserGroup);
        femaleButton.setToggleGroup(sexChooserGroup);
    }

    /**
     * Method to set the color of the input fields black, or change it red if error
     * @param errorField    field to change
     */
    public void setInvalidField(String errorField) {
        switch (errorField) {
            case "password" -> {
                errorString.append("- Invalid Password: must be 8 characters long and contain at least one special character, one lowercase, one uppercase and a number \n");
                lblPassword.setStyle("-fx-text-fill: red;");
                txtPassword.setStyle("-fx-text-fill: red;");
            }
            case "email" -> {
                errorString.append("- Email already used or invalid email format \n");
                lblEmail.setStyle("-fx-text-fill: red;");
                txtEmail.setStyle("-fx-text-fill: red;");
            }
            case "username" -> {
                errorString.append("- Username already used or invalid username format \n");
                lblUsername.setStyle("-fx-text-fill: red;");
                txtUsername.setStyle("-fx-text-fill: red;");
            }
            case "phoneNumber" -> {
                errorString.append("- Invalid phoneNumber");
                lblPhoneNumber.setStyle("-fx-text-fill: red;");
                txtPhoneNumber.setStyle("-fx-text-fill: red;");
            }
            case "surname" -> {
                errorString.append("- Invalid surname format \n");
                lblSurname.setStyle("-fx-text-fill: red;");
                txtSurname.setStyle("-fx-text-fill: red;");
            }
            case "name" -> {
                errorString.append("- Invalid name format \n");
                lblName.setStyle("-fx-text-fill: red;");
                txtName.setStyle("-fx-text-fill: red;");
            }
            case "province" -> {
                errorString.append("- Invalid province format \n");
                lblProvince.setStyle("-fx-text-fill: red;");
            }
            case "birthPlace" -> {
                errorString.append("- Invalid birthplace format \n");
                lblBirthPlace.setStyle("-fx-text-fill: red;");
                txtBirthPlace.setStyle("-fx-text-fill: red;");
            }
            case "birthDate" -> {
                errorString.append("- Invalid birthDate: date impossible or empty");
                lblBirthDate.setStyle("-fx-text-fill: red;");
            }
            case "domicile" -> {
                errorString.append("- Invalid domicile format \n");
                lblDomicile.setStyle("-fx-text-fill: red;");
                txtDomicile.setStyle("-fx-text-fill: red;");
            }
            case "sex" -> {
                errorString.append("- Invalid sex input \n");
                lblSex.setStyle("-fx-text-fill: red;");
            }
            case "taxIDCode" -> {
                errorString.append("- Invalid taxIDCode: inconsistent with other data \n");
                lblTaxIDCode.setStyle("-fx-text-fill: red;");
                txtTaxIDCode.setStyle("-fx-text-fill: red;");
            }
            case "refDoc" -> {
                errorString.append("- Invalid medico referente choose \n");
                lblRefDoc.setStyle("-fx-text-fill: red;");
            }
            default -> {    // Set all fields color's black
                lblPassword.setStyle("-fx-text-fill: white;");
                txtPassword.setStyle("-fx-text-fill: black;");
                lblEmail.setStyle("-fx-text-fill: white;");
                txtEmail.setStyle("-fx-text-fill: black;");
                lblUsername.setStyle("-fx-text-fill: white;");
                txtUsername.setStyle("-fx-text-fill: black;");
                lblPhoneNumber.setStyle("-fx-text-fill: white;");
                txtPhoneNumber.setStyle("-fx-text-fill: black;");
                lblName.setStyle("-fx-text-fill: white;");
                txtName.setStyle("-fx-text-fill: black;");
                lblSurname.setStyle("-fx-text-fill: white;");
                txtSurname.setStyle("-fx-text-fill: black;");
                lblProvince.setStyle("-fx-text-fill: white;");
                lblBirthPlace.setStyle("-fx-text-fill: white;");
                txtBirthPlace.setStyle("-fx-text-fill: black;");
                lblBirthDate.setStyle("-fx-text-fill: white;");
                lblDomicile.setStyle("-fx-text-fill: white;");
                txtDomicile.setStyle("-fx-text-fill: black;");
                lblSex.setStyle("-fx-text-fill: white;");
                lblTaxIDCode.setStyle("-fx-text-fill: white;");
                txtTaxIDCode.setStyle("-fx-text-fill: black;");
                lblRefDoc.setStyle("-fx-text-fill: white;");
                wrongRegistration.setText("");      // Hide error message
                errorString = new StringBuilder();
            }
        }

        // Set error message
        if(!errorField.contains("resetAll"))
            wrongRegistration.setText("Registrazione fallita");

        errorCodeLabel.setOpacity(1);
        errorCodeLabel.setVisible(true);
        errorCodeLabel.setText(errorString.toString());     // Set label with error code description
    }

    public void handleRegistration(ActionEvent actionEvent) throws IOException {
        setInvalidField("resetAll");    // Set all fields colors black

        int index = comboBoxRefDoc.getItems().indexOf(comboBoxRefDoc.getValue());
        int docID = -1;
        char sex = 'Z';

        // Check if a doctor has been selected
        if(index != -1)
            docID = doctorID.get(comboBoxRefDoc.getItems().indexOf(comboBoxRefDoc.getValue()));

        System.out.println("Index1 --> " + index + "\nDoctorID --> " + docID);

        // If a sex has been chosen
        if(sexChooserGroup.getSelectedToggle() != null)
            sex = ((RadioButton) sexChooserGroup.getSelectedToggle()).getText().charAt(0);

        Patient patient = new Patient(this, txtName.getText(), txtSurname.getText(), txtEmail.getText(), txtPhoneNumber.getText(), txtUsername.getText(), txtPassword.getText(), txtBirthPlace.getText(), comboBoxProvince.getValue(), txtBirthDate.getValue(), txtDomicile.getText(), sex, txtTaxIDCode.getText(), docID);

        // If all the fields are correct
        if(patient.getCheck()) {
            // Allert for registration success and change scene to Login
            newScene.showAlert("Registration success", "La registrazione è avvenuta con successo", Alert.AlertType.INFORMATION);
            newScene.changeScene("Login.fxml", "Paziente", actionEvent);
        }
    }

    public void handleAnnulla(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("Login.fxml", "Paziente", actionEvent);
    }
}
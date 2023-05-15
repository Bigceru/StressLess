package it.univr.telemedicina.controller;

import it.univr.telemedicina.MainApplication;
import it.univr.telemedicina.Patient;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Registration implements Initializable {
    private final MainApplication newScene = new MainApplication();
    private ToggleGroup sexChooserGroup;
    @FXML
    private ComboBox<String> comboBoxRefDoc;
    @FXML
    private ComboBox<String> comboBoxProvince;
    @FXML
    private RadioButton maleButton;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtBirthPlace;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDomicile;
    @FXML
    private TextField txtTaxIDCode;
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
    private Label lblEmail;
    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPhoneNumber;
    private static ArrayList<Integer> doctorID = new ArrayList<>();

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
                lblPassword.setStyle("-fx-text-fill: red;");
                txtPassword.setStyle("-fx-text-fill: red;");
            }
            case "email" -> {
                lblEmail.setStyle("-fx-text-fill: red;");
                txtEmail.setStyle("-fx-text-fill: red;");
            }
            case "username" -> {
                lblUsername.setStyle("-fx-text-fill: red;");
                txtUsername.setStyle("-fx-text-fill: red;");
            }
            case "phoneNumber" -> {
                lblPhoneNumber.setStyle("-fx-text-fill: red;");
                txtPhoneNumber.setStyle("-fx-text-fill: red;");
            }
            default -> {    // Set all fields color's black
                lblPassword.setStyle("-fx-text-fill: black;");
                txtPassword.setStyle("-fx-text-fill: black;");
                lblEmail.setStyle("-fx-text-fill: black;");
                txtEmail.setStyle("-fx-text-fill: black;");
                lblUsername.setStyle("-fx-text-fill: black;");
                txtUsername.setStyle("-fx-text-fill: black;");
                lblPhoneNumber.setStyle("-fx-text-fill: black;");
                txtPhoneNumber.setStyle("-fx-text-fill: black;");
            }
        }
    }

    public void handleRegistration(ActionEvent actionEvent) throws IOException {
        setInvalidField("resetAll");    // Set all fields colors black

        int docID = doctorID.get(comboBoxRefDoc.getItems().indexOf(comboBoxRefDoc.getValue()));
        int index1 = comboBoxRefDoc.getItems().indexOf(comboBoxRefDoc.getValue());
        int index2 = comboBoxRefDoc.getSelectionModel().getSelectedIndex();
        System.out.println("Index1 --> " + index1 + "\nIndex2 --> " + index2 +"\nDoctor ID --> " + docID);

        Patient patient = new Patient(this, txtName.getText(), txtSurname.getText(), txtEmail.getText(), txtPhoneNumber.getText(), txtUsername.getText(), txtPassword.getText(), txtBirthPlace.getText(), comboBoxProvince.getValue(), txtBirthDate.getValue(), txtDomicile.getText(), ((RadioButton) sexChooserGroup.getSelectedToggle()).getText().charAt(0), txtTaxIDCode.getText(), docID);

        // If all the fields are correct
        if(patient.getCheck())
            newScene.changeScene("PatientLogin.fxml", "Paziente", actionEvent);
    }

    public void handleAnnulla(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("PatientLogin.fxml", "Paziente", actionEvent);
    }
}
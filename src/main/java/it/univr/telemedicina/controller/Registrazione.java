package it.univr.telemedicina.controller;

import it.univr.telemedicina.HelloApplication;
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

public class Registrazione implements Initializable {
    @FXML
    private TextField txtMedicoRef;
    private final HelloApplication newScene = new HelloApplication();

    @FXML
    private ComboBox<String> comboBoxBoxProvincia;
    private ToggleGroup sexChooserGroup;
    @FXML
    private RadioButton maleButton;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCognome;
    @FXML
    private TextField txtLuogoNascita;
    @FXML
    private TextField txtNumTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDomicilio;
    @FXML
    private TextField txtCF;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtNomeUtente;
    @FXML
    private DatePicker txtData;
    @FXML
    private Label wrongRegistration;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Database database = new Database(1);
            ArrayList<String> list = database.getQuery("SELECT Sigla FROM Provincie", new String[]{"Sigla"});   // Fai una query per ottenere tutte le sigle delle province
            comboBoxBoxProvincia.setEditable(true);
            comboBoxBoxProvincia.getItems().addAll(list);
            comboBoxBoxProvincia.setVisibleRowCount(5);
            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Set RadioButton Group
        sexChooserGroup = new ToggleGroup();
        maleButton.setToggleGroup(sexChooserGroup);
        femaleButton.setToggleGroup(sexChooserGroup);

    }

    public void handleRegistrati(ActionEvent actionEvent) throws IOException {

        Patient patient = new Patient(txtNome.getText(), txtCognome.getText(), txtEmail.getText(), txtNumTelefono.getText(), txtNomeUtente.getText(), txtPassword.getText(), txtLuogoNascita.getText(), comboBoxBoxProvincia.getValue(), txtData.getValue(), txtDomicilio.getText(), ((RadioButton) sexChooserGroup.getSelectedToggle()).getText().charAt(0), txtCF.getText(), Integer.parseInt(txtMedicoRef.getText()));

        if(!patient.getCheck()){
            txtPassword.setStyle("-fx-text-fill: red;");
            wrongRegistration.setText("Registrazione Fallita");
        }

        newScene.changeScene("loginPaziente.fxml", "Paziente", actionEvent);
    }

    public void handleAnnulla(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("loginPaziente.fxml", "Paziente", actionEvent);
    }

    public void handleSexM(ActionEvent actionEvent) {
    }

    public void handleSexF(ActionEvent actionEvent) {
    }

    public void handleSexAltro(ActionEvent actionEvent) {
    }

    private boolean checkError() {
        return true;
    }
}
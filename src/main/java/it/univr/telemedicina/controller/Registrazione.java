package it.univr.telemedicina.controller;

import it.univr.telemedicina.HelloApplication;
import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Registrazione implements Initializable {
    @FXML
    private TextField txtMedicoRef;
    private HelloApplication newScene = new HelloApplication();
    private DatabaseManager db = new DatabaseManager();
    @FXML
    private ChoiceBox<String> choiceBoxProvincia;
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
    private TextField txtPassword;
    @FXML
    private TextField txtNomeUtente;
    @FXML
    private DatePicker txtData;
    private LinkedList<String> datiUtente = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Database database = new Database("codiceCatastale.db");

            ArrayList<String> list = database.getQuery("SELECT Sigla FROM Provincie", new String[]{"Sigla"});   // Fai una query per ottenere tutte le sigle delle province
            /*
            ResultSet rs = pstmt.executeQuery();
            List<String> nomi = new ArrayList<>();
            while (rs.next()) {
            String nome = rs.getString("Nome");
            nomi.add(nome);
            }
             */
            choiceBoxProvincia.getItems().setAll(list);

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleRegistrati(ActionEvent actionEvent) throws IOException {
        datiUtente.add(txtNome.getText());              //  Nome
        datiUtente.add(txtCognome.getText());           //  Cognome
        datiUtente.add(txtLuogoNascita.getText());      //  Luogo di nascita
        datiUtente.add(choiceBoxProvincia.getValue());         //  Provincia
        datiUtente.add(txtNumTelefono.getText());       //  Numero di telefono
        datiUtente.add(txtEmail.getText());             //  E-mail
        datiUtente.add(txtDomicilio.getText());         //  Domicilio
        datiUtente.add(txtCF.getText());                //  Codice fiscale
        datiUtente.add(txtNome.getText());              //  Nome utente
        datiUtente.add(txtPassword.getText());          //  Password
        datiUtente.add(txtMedicoRef.getText());            //  Medico referente

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
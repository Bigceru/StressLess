package it.univr.telemedicina.controller;

import it.univr.telemedicina.HelloApplication;
import it.univr.telemedicina.utilities.DatabaseManager;
import it.univr.telemedicina.utilities.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPaziente {
    private HelloApplication newScene = new HelloApplication();
    private DatabaseManager db = new DatabaseManager();

    @FXML
    private TextField txtUserName;
    @FXML
    private TextField txtPassName;

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String userName = txtUserName.getText();
        String passName = txtPassName.getText();

        try {
            Database database = new Database(2);
            database.getQuery("SELECT * FROM Pazienti WHERE NomeUtente = ?".replaceAll("\\?", userName), new String[]{"NomeUtente"});
            database.closeAll();
        } catch (SQLException e) {
            System.out.println("Username doesn't exist!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        if(userName.isEmpty() || passName.isEmpty() ){
            showAlert("Campi non pieni","Compila tutti i campi");
        }
        else {
            try (Connection conn = db.getConnection();
                 PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Pazienti WHERE NomeUtente = ?")) {
                stmt1.setString(1, userName);
                ResultSet rs = stmt1.executeQuery();
                if (rs.next()) {
                    PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM Pazienti WHERE NomeUtente = ? AND Password = ?");
                    stmt2.setString(1, userName);
                    stmt2.setString(2, passName);
                    rs = stmt2.executeQuery();
                    if (rs.next()) {
                        newScene.changeScene("paginaUtente.fxml", "Pagina utente", actionEvent);
                    } else
                        showAlert("Errore password", "Password non corretta");
                } else
                    showAlert("Errore", "Credenziali errate");
            } catch (SQLException e) {
                showAlert("Database", "Database non trovato");
                throw new RuntimeException(e);
            }
        }
    }

    public void handleRegistratiButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("registrazione.fxml", "Registrazione utente", actionEvent);
    }

    public void handleIndietroButton(ActionEvent actionEvent) throws IOException {
        newScene.changeScene("login.fxml", "Identificazione utente", actionEvent);
    }

    //Metodo per il pop-up di errore
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

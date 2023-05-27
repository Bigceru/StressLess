package it.univr.telemedicina.controller.chat;

import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.users.User;
import it.univr.telemedicina.utilities.Database;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatMenuController implements Initializable {
    @FXML
    public ListView<String> listContactChat;
    @FXML
    public ListView<String> listChat;
    @FXML
    public TextField txtMessage;
    @FXML
    public Button sendMessageButton;

    private static User user;


    // Initialize Chat menu with contacts
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Database database = new Database(2);

            // If I'm Doctor I need all my patients
            if(user instanceof Doctor)
                contacts.addAll(database.getQuery("SELECT ID, Name, Surname, Username FROM Patients WHERE refDoc = " + ((Doctor) user).getID(), new String[]{"ID", "Name", "Surname", "Username"}));
            // I'm  Patient, i need my doctor
            else
                contacts.addAll(database.getQuery("SELECT ID, Name, Surname, Username FROM Doctors WHERE ID = " + ((Patient) user).getRefDoc(), new String[]{"ID", "Name", "Surname", "Username"}));

            // Take the id of who is opening the chat
            userId = ((user instanceof Doctor) ? ((Doctor)user).getID() : ((Patient)user).getPatientID());

            // Cycle for add all contacts in the chat
            for(int i = 0; i < contacts.size()-3; i += 4) {
                listContactChat.getItems().add(contacts.get(i + 1) + " " + contacts.get(i + 2) + " (" + contacts.get(i + 3) + ")");

                // If this contact is in the list of message to be read set it with red dot
                // if (messageToReadQuery.contains(contacts.get(0));
            }

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        changeConversation();

        // Call the method to update contact list property
        updateListContacts();
    }

    private void changeConversation(){
        //Set a listener
        listContactChat.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            listChat.getItems().clear();
            //listChat.setBackground(null);
            listChat.getItems().add("Chat con " + newValue);  // Set the selected chat to main listview

            // Add the old conversation
            System.out.println(newValue);
            setChatMessages(newValue);

            // Set sendMessageButton to invisibe if I'm in the system chat
            sendMessageButton.setVisible(contactId != -1);

            // Call the method to update contact list property
            updateListContacts();
        });
    }

    public void setUser(User user){
        ChatMenuController.user = user;
    }
}

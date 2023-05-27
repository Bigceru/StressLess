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
    private int userId;
    private int contactId;
    private ArrayList<String> contacts = new ArrayList<>(Arrays.asList("-1", "SISTEMA", "", "Server"));     // Add System chat to contacts

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

    private void updateListContacts() {
        try {
            Database database = new Database(2);

            // Query to set emailIcon
            ArrayList<String> messageToReadQuery = database.getQuery("SELECT Sender FROM Chat WHERE Receiver = " + userId + " AND ReadFlag = 0", new String[]{"Sender"});
            listContactChat.setCellFactory(param -> new ListCell<>() {
                private Circle circle = new Circle(5);

                {
                    circle.setFill(Color.DARKRED);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if(item != null) {
                        for(int i = 0; i < contacts.size()-3; i += 4) {
                            String username = item.split("\\(")[1].split("\\)")[0];

                            // If this contact has send a message that has to be read by user AND If I'm editing the contact associated with the item
                            if (messageToReadQuery.contains(contacts.get(i)) && username.equals(contacts.get(i+3))){
                                System.out.println("Item --> " + item);
                                setGraphic(circle);
                                break;
                            }
                        }
                        setText(item);
                    }
                }
            });

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setChatMessages(String newValue) {
        listChat.getItems().clear();

        // Add the old conversation
        try {
            Database database = new Database(2);


            // take the username
            String username = newValue.split("\\(")[1].split("\\)")[0];
            System.out.println("Username --> " + username);

            // Take the id of the contact
            userId = ((user instanceof Doctor) ? ((Doctor)user).getID() : ((Patient)user).getPatientID());

            // If the persone selected System chat
            if(newValue.equals("SISTEMA  (Server)"))
                contactId = -1;
            else if(user instanceof Doctor)
                contactId = Integer.parseInt(database.getQuery("SELECT ID FROM Patients WHERE Username = '" + username + "'", new String[]{"ID"}).get(0));
            else
                contactId = ((Patient) user).getRefDoc();

            // Do a query to get all the messages between userId and contactId
            ArrayList<String> resultConversationQuery = database.getQuery("SELECT ID, Sender, Receiver, Text FROM Chat WHERE (Sender = " + userId + " AND Receiver = " + contactId + ") OR (Sender = " + contactId + " AND Receiver = " + userId + ") ORDER BY ID ASC", new String[]{"ID", "Sender", "Receiver", "Text"});

            // Update query to set all message of the chat as read
            database.updateQuery("Chat", Map.of("ReadFlag", 1), Map.of("Sender", contactId, "Receiver", userId));

            // Fill the chat with all old message
            for(int i = 0; i < resultConversationQuery.size() - 3; i += 4){
                // If I'm the sender
                if(Integer.parseInt(resultConversationQuery.get(i+1)) == (userId)){
                    listChat.getItems().add("Io: " + resultConversationQuery.get(i+3));

                }   // The other user is the sender
                else {
                    listChat.getItems().add(username + ": " + resultConversationQuery.get(i + 3));
                }
            }

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleSendMessage(ActionEvent actionEvent){
        // If there is a message
        if(!txtMessage.getText().isEmpty()) {
            try {
                Database database = new Database(2);

                // Query to insert the new message in database
                database.insertQuery("Chat", new String[]{"Sender", "Receiver", "Text", "ReadFlag"}, new Object[]{userId, contactId, txtMessage.getText(), 0});

                // Add new message
                listChat.getItems().add("Io: " + txtMessage.getText());

                database.closeAll();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setUser(User user){
        ChatMenuController.user = user;
    }
}

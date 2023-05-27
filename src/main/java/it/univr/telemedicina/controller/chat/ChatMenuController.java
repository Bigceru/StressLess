package it.univr.telemedicina.controller.chat;

import it.univr.telemedicina.users.Doctor;
import it.univr.telemedicina.users.Patient;
import it.univr.telemedicina.users.User;
import it.univr.telemedicina.utilities.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatMenuController implements Initializable {
    @FXML
    public ListView listContactChat;
    @FXML
    public ListView listChat;

    private static User user;


    // Initialize Chat menu with contacts
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            Database database = new Database(2);
            ArrayList<String> contacts = new ArrayList<>();
            //If i'm Doctor i need all my patients
            if(user instanceof Doctor)
                contacts = database.getQuery("SELECT Name, Surname FROM Patients WHERE refDoc = " + ((Doctor)user).getID(), new String[]{"Name","Surname"});
                //I'm  Patient, i need my doctor
            else
                contacts = database.getQuery("SELECT Name, Surname FROM Doctors WHERE IDPatient = " + ((Patient)user).getPatientID(), new String[]{"Name","Surname"});

            for(int i = 0; i < contacts.size()-1; i = i +2)
                listContactChat.getItems().add(contacts.get(i) + " " + contacts.get(i+1));



            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }




    }

    public void setUser(User user){
        this.user = user;
    }
}

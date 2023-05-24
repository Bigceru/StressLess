package it.univr.telemedicina.users;

import it.univr.telemedicina.controller.RegistrationController;
import it.univr.telemedicina.utilities.Database;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class User {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private boolean check;

    // Main Constructor
    public User(String name, String surname, String email, String phoneNumber, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    // Constructor for table view in Doctor home
    public User(String name, String surname) {
        this.name = name + " " + surname;
        this.surname = surname;
    }

    //True if exists
    //False if not exists
    //Is public for ChangeDataController
    public boolean alreadyExist(String table, String fieldToCheck, String inputField){
        boolean tempCheck = false;
        try {
            Database db = new Database(2);
            ArrayList<String> test = db.getQuery("SELECT * FROM" + table + "WHERE " + fieldToCheck +" = " + "\"" + inputField + "\"", new String[]{fieldToCheck});

            if (!test.isEmpty())
                tempCheck = true;

        } catch (SQLException e) {
            System.out.println("SQL Access error");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return tempCheck;
    }

    protected abstract boolean checkName(String name);
    protected abstract boolean checkSurname(String surname);
    public abstract boolean checkPhoneNumber(String phoneNumber);
    public abstract boolean checkEmail(String email);
    public abstract boolean checkUsername(String username);
    public abstract boolean checkPassword(String password);

    public abstract boolean getCheck();

    // Get and set methods
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "\nName: " + getName() + "\nSurname: " + getSurname() + "\nEmail: " + getEmail() + "\nPhone: " + getPhoneNumber() + "\nPassword: " + getPassword() + "\nUsername" + getUsername() + "\n";

    }
}

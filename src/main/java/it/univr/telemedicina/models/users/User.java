package it.univr.telemedicina.models.users;

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

    /***
     * User (Doctor, Patient) constructor
     * @param name User name
     * @param surname User surname
     * @param email User email
     * @param phoneNumber User phoneNumber
     * @param username User username
     * @param password User password
     */
    // Main Constructor
    public User(String name, String surname, String email, String phoneNumber, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    /***
     * Constructor for table view in Doctor Home
     * @param name User name
     * @param surname User surname
     */
    // Constructor for table view in Doctor home
    public User(String name, String surname) {
        this.name = name + " " + surname;
        this.surname = surname;
    }

    /**
     * Check if User already exist
     * @param table database table name
     * @param fieldToCheck field to check in the table
     * @param inputField input of the field
     * @return already exist value
     */
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

    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param name User name
     * @return check result
     */
    protected abstract boolean checkName(String name);
    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param surname User surname
     * @return check result
     */
    protected abstract boolean checkSurname(String surname);
    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param phoneNumber User phoneNumber
     * @return check result
     */
    public abstract boolean checkPhoneNumber(String phoneNumber);
    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param email User email
     * @return check result
     */
    public abstract boolean checkEmail(String email);
    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param username User username
     * @return check result
     */
    public abstract boolean checkUsername(String username);
    /**
     * Abstract declaration for User implemented in Doctor and Patient
     * @param password User password
     * @return check result
     */
    public abstract boolean checkPassword(String password);

    /**
     * Get user total check
     * @return Check result
     */
    public abstract boolean getCheck();

    /**
     * Take Name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Take Surname
     * @return surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Take email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     * @return email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get phone number
     * @return phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set phone number
     * @return phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get user Username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get user password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set user password
     * @return password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Set user username
     * @return username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * User to string method
     * @return user toString
     */
    @Override
    public String toString() {
        return "\nName: " + getName() + "\nSurname: " + getSurname() + "\nEmail: " + getEmail() + "\nPhone: " + getPhoneNumber() + "\nPassword: " + getPassword() + "\nUsername" + getUsername() + "\n";

    }
}

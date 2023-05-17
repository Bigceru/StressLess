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
    private final RegistrationController reg;

    // Constructor
    public User(RegistrationController reg, String name, String surname, String email, String phoneNumber, String username, String password) {
        this.reg = reg;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;

        if(reg != null) {
            // Check if name, surname, email, password and phone number are correct
            check = checkName(name) & checkSurname(surname) & checkEmail(email) & checkPassword(password) & checkPhoneNumber(phoneNumber) & checkUsername(username);

        }
        else
            check = true;
        }

    //True if exists
    //False if not exists
    protected boolean alreadyExist(String fieldToCheck, String inputField){
        boolean tempCheck = false;

        try {
            Database db = new Database(2);
            ArrayList<String> test = db.getQuery("SELECT * FROM Patients WHERE " + fieldToCheck +" = " + "\"" + inputField + "\"", new String[]{fieldToCheck});

            if (!test.isEmpty())
                tempCheck = true;

        } catch (SQLException e) {
            System.out.println("SQL Access error");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return tempCheck;
    }

    protected boolean checkName(String name){
        boolean tempCheck = !name.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("name");

        return tempCheck;
    }

    protected boolean checkSurname(String surname){
        boolean tempCheck = !surname.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("surname");

        return tempCheck;
    }

    protected boolean checkPhoneNumber(String phoneNumber){
        boolean tempCheck = phoneNumber.matches("^[0-9]{10,15}$") && !alreadyExist("phoneNumber", phoneNumber);

        if (!tempCheck)
            reg.setInvalidField("phoneNumber");

        return tempCheck;
    }

    protected boolean checkEmail(String email) {
        boolean tempCheck = email.contains("@") && email.contains(".") && !alreadyExist("email", email);

        if (!tempCheck)
            reg.setInvalidField("email");

        return tempCheck;
    }

    protected boolean checkUsername(String username) {
        // looking for Username already exist or is empty
        boolean tempCheck = !username.isEmpty() && !alreadyExist("username",username);

        if(!tempCheck){
            reg.setInvalidField("username");
        }
        return tempCheck;
    }

    protected boolean checkPassword(String password) {
        boolean tempCheck = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.\\-_:,;])(?=\\S+$).{8,}$");

        if (!tempCheck)
            reg.setInvalidField("password");

        return tempCheck;
        /*
        ^                 # start-of-string
        (?=.*[0-9])       # a digit must occur at least once
        (?=.*[a-z])       # a lower case letter must occur at least once
        (?=.*[A-Z])       # an upper case letter must occur at least once
        (?=.*[@#$%^&+=])  # a special character must occur at least once
        (?=\S+$)          # no whitespace allowed in the entire string
        .{8,}             # anything, at least eight places though
        $                 # end-of-string
         */
    }

    public boolean getCheck() {
        return check;
    }

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

    @Override
    public String toString() {
        return "\nName: " + getName() + "\nSurname: " + getSurname() + "\nEmail: " + getEmail() + "\nPhone: " + getPhoneNumber() + "\nPassword: " + getPassword() + "\nUsername" + getUsername() + "\n";

    }
}

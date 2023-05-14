package it.univr.telemedicina;

public abstract class User {
    private String name;
    private String surname;
    private String email;
    private String numTelephone;
    private String username;
    private String password;
    private boolean check;

    // Constructor
    public User(String name, String surname, String email, String numTelephone, String username, String password){
        // Check if email, password and phone number are correct
        check = checkEmail(email) && checkPassword(password) && checkTelephone(numTelephone);

        // If they are correct
        if(check) {
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.numTelephone = numTelephone;
            this.username = username;
            this.password = password;
        }
    }

    private boolean checkEmail(String email) {
        boolean tempCheck = email.contains("@") && email.contains(".");

        return tempCheck;
    }

    private boolean checkTelephone(String numTelephone){
        boolean tempCheck = numTelephone.matches("^[0-9]{10,15}$");

        return tempCheck;
    }

    private boolean checkPassword(String password) {
        boolean tempCheck = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

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

    public void setName(String name) {
        this.name = name;
    }


    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getNumTelephone() {
        return numTelephone;
    }

    public void setNumTelephone(String numTelephone) {
        this.numTelephone = numTelephone;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

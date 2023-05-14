package it.univr.telemedicina;

import java.util.ArrayList;

public abstract class User {
    private String name;
    private String surname;
    private String email;
    private String numTelephone;
    private String username;
    private String password;

    // Constructor
    public User(String name, String surname, String email, String numTelephone, String username, String password){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.numTelephone = numTelephone;
        this.username = username;
        this.password = password;
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

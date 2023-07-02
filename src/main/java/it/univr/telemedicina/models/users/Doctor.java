package it.univr.telemedicina.models.users;

import it.univr.telemedicina.utilities.Database;

import java.sql.SQLException;

public class Doctor extends User{
    private boolean check;

    /***
     * Doctor constructor
     * @param name Doctor name
     * @param surname Doctor surname
     * @param email Doctor email
     * @param phoneNumber Doctor Phone Number
     * @param username Doctor Username
     * @param password Doctor Password
     */
    public Doctor(String name, String surname, String email, String phoneNumber, String username, String password) {
        super(name, surname, email, phoneNumber, username, password);
    }

    /***
     * Validation check for doctor name
     * @param name String to verify if is not empty
     * @return verification success
     */
    @Override
    protected boolean checkName(String name) {
         return !name.isEmpty();
    }

    /***
     * Validation check for doctor surname
     * @param surname String to verify if is not empty
     * @return verification success
     */
    @Override
    protected boolean checkSurname(String surname) {
        return !surname.isEmpty();
    }

    /***
     * Validation check for the phone number
     * @param phoneNumber String to verify
     * @return verification success
     */
    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{11}$") && !alreadyExist("Doctors", "phoneNumber", phoneNumber);
    }

    /***
     * Validation check for the email
     * @param email String to verify
     * @return verification success
     */
    @Override
    public boolean checkEmail(String email) {
        return email.contains("@") && email.contains(".") && email.substring(email.indexOf("@")).contains("hospital")  && !alreadyExist("Doctors", "email", email);
    }

    /***
     * Validation check for username
     * @param username String to verify if is not empty and not used
     * @return verification success
     */
    @Override
    public boolean checkUsername(String username) {
        // looking for Username already exist or is empty
        return !username.isEmpty() && !alreadyExist("Doctors", "username", username);
    }

    /***
     * Password verification (if matches all the requirements)
     * @param password String to verify
     * @return verification success
     */
    @Override
    public boolean checkPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.\\-_:,;])(?=\\S+$).{8,}$");
    }

    /***
     *
     * @return true if all the checks are passed
     */
    @Override
    public boolean getCheck() {
        return check;
    }

    /***
     * Take doctorID from database
     * @return id
     */
    public int getID(){
        try {
            Database db = new Database(2);
            int id = Integer.parseInt(db.getQuery("SELECT ID FROM Doctors WHERE username = \"" + getUsername() + "\"", new String[]{"ID"}).get(0));
            db.closeAll();
            return id;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

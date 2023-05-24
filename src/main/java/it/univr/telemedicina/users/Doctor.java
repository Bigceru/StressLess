package it.univr.telemedicina.users;

public class Doctor extends User{
    private boolean check;

    public Doctor(String name, String surname, String email, String phoneNumber, String username, String password) {
        super(name, surname, email, phoneNumber, username, password);
    }

    @Override
    protected boolean checkName(String name) {
         return !name.isEmpty();
    }

    @Override
    protected boolean checkSurname(String surname) {
        return !surname.isEmpty();
    }

    @Override
    public boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{11}$") && !alreadyExist("Doctors", "phoneNumber", phoneNumber);
    }

    @Override
    public boolean checkEmail(String email) {
        return email.contains("@") && email.contains(".") && email.substring(email.indexOf("@")).contains("hospital")  && !alreadyExist("Doctors", "email", email);
    }

    @Override
    public boolean checkUsername(String username) {
        // looking for Username already exist or is empty
        return !username.isEmpty() && !alreadyExist("Doctors", "username", username);
    }

    @Override
    public boolean checkPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.\\-_:,;])(?=\\S+$).{8,}$");
    }

    @Override
    public boolean getCheck() {
        return check;
    }



    public void update(){

    }
}

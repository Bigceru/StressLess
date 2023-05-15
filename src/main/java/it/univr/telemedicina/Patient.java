package it.univr.telemedicina;

import it.univr.telemedicina.controller.Registration;
import it.univr.telemedicina.utilities.Database;
import java.sql.SQLException;
import java.time.LocalDate;

public class Patient extends User {

    private final static String[] stringFields = {"name", "surname", "birthPlace", "province", "birthDate", "phoneNumber", "domicile", "sex", "taxIDCode", "email", "username", "password", "refDoc"};

    private String birthPlace;
    private String province;
    LocalDate birthDate;
    private String domicile;
    private char sex;
    private String taxIDCode;
    private int refDoc;

    //Constructor
    public Patient(Registration reg, String name, String surname, String email, String phoneNumber, String username, String password, String birthPlace, String province, LocalDate birthDate, String domicile, char sex, String taxIDCode, int refDoc){
        super(reg, name, surname, email, phoneNumber, username, password);

        if(getCheck()) {
            // If the previous field were correct
            System.out.println("Check superato!!!");

            this.birthPlace = birthPlace;
            this.province = province;
            this.birthDate = birthDate;
            this.domicile = domicile;
            this.sex = sex;
            this.taxIDCode = taxIDCode;
            this.refDoc = refDoc;

            // INSERT into database
            try {
                System.out.println("Provo ad entrare nel Database");
                Database database = new Database(2);
                database.insertQuery("Patients", stringFields, new Object[]{name,surname, birthPlace, province, birthDate, phoneNumber, domicile, sex, taxIDCode, email, username, password, refDoc});
            } catch (SQLException e){
                System.out.println("Error SQL query!");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    //Check TaxIdCode
    private boolean checkTaxIdCode() {
        boolean tempCheck = true;

        return tempCheck;
    }

    // Get and set methods
    public String getBirthPlace() {
        return birthPlace;
    }

    public String getProvince() {
        return province;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getDomicile() {
        return domicile;
    }

    public char getSex() {
        return sex;
    }

    public String getTaxIDCode() {
        return taxIDCode;
    }

    public int getRefDoc() {
        return refDoc;
    }
}

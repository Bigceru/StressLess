package it.univr.telemedicina;
import it.univr.telemedicina.utilities.Database;
import javafx.scene.chart.PieChart;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class Patient extends User {

    private final static String[] stringFields = {"Nome", "Cognome", "LuogoDiNascita", "Provincia", "DataDiNascita", "NumeroDiTelefono", "Domicilio", "Sesso", "CodiceFiscale", "Email", "NomeUtente", "Password", "MedicoReferente"};

    private String birthPlace;
    private String province;
    LocalDate birthDate;
    private String domicile;
    private char sex;
    private String codiceFiscale;
    private int refDoc;

    //Constructor
    public Patient(String name, String surname, String email, String numTelephone, String username, String password, String birthPlace, String province, LocalDate birthDate, String domicile, char sex, String codiceFiscale, int refDoc){
        super(name, surname, email, numTelephone, username, password);

        // If the previous field were correct
        if(getCheck()) {
            this.birthPlace = birthPlace;
            this.province = province;
            this.birthDate = birthDate;
            this.domicile = domicile;
            this.sex = sex;
            this.codiceFiscale = codiceFiscale;
            this.refDoc = refDoc;

            // INSERT into database
            try {
                Database database = new Database(2);
                database.insertQuery("Pazienti", stringFields, new Object[]{name, surname, birthPlace, province, birthDate, numTelephone, domicile, sex, codiceFiscale, email, username, password, refDoc});
            } catch (SQLException e){
                System.out.println("Error SQL query!");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    // Get and set methods
    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public int getRefDoc() {
        return refDoc;
    }

    public void setRefDoc(int refDoc) {
        this.refDoc = refDoc;
    }


}

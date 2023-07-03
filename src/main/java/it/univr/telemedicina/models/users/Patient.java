package it.univr.telemedicina.models.users;

import it.univr.telemedicina.controller.patient.RegistrationController;
import it.univr.telemedicina.utilities.Database;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Patient extends User {

    private final static String[] stringFields = {"name", "surname", "birthPlace", "province", "birthDate", "phoneNumber", "domicile", "sex", "taxIDCode", "email", "username", "password", "refDoc", "registration"};
    private String birthPlace;
    private String province;
    LocalDate birthDate;
    private String domicile;
    private char sex;
    private String taxIDCode;
    private int refDoc;
    private final RegistrationController reg;
    private boolean check;
    private String therapy;     // Field for table view

    /**
     * Constructor for the table view in Doctor Home
     * @param name Patient name
     * @param surname Patient surname
     * @param therapy Patient therapy
     * @param reg Patient registration
     */
    public Patient(String name, String surname, String therapy, RegistrationController reg){
        super(name, surname);
        this.reg = reg;
        this.therapy = therapy;
    }

    /**
     * Patient constructor with check for existing values
     * @param reg Patient registration
     * @param name Patient name
     * @param surname Patient surname
     * @param email Patient email
     * @param phoneNumber Patient phone-number
     * @param username Patient username
     * @param password Patient password
     * @param birthPlace Patient birth-palce
     * @param province Patient province
     * @param birthDate Patient birthdate
     * @param domicile Patient domicile
     * @param sex Patient sex
     * @param taxIDCode Patient tax id code
     * @param refDoc Patient doctor reference
     */
    // Main Constructor
    public Patient(RegistrationController reg, String name, String surname, String email, String phoneNumber, String username, String password, String birthPlace, String province, LocalDate birthDate, String domicile, char sex, String taxIDCode, int refDoc){
        super(name, surname, email, phoneNumber, username, password);

        this.reg = reg;
        this.birthPlace = birthPlace;
        this.province = province;
        this.birthDate = birthDate;
        this.domicile = domicile;
        this.sex = sex;
        this.taxIDCode = taxIDCode;
        this.refDoc = refDoc;

        if(reg != null) {
            check = checkName(name) & checkSurname(surname) & checkEmail(email) & checkPassword(password) & checkPhoneNumber(phoneNumber) & checkUsername(username) & checkProvince(province) & checkDomicile(domicile) & checkSex(sex) & checkBirthDate(birthDate) & checkBirthPlace(birthPlace) & checkRefDoc(refDoc) & checkTaxIdCode(taxIDCode);

            if (this.getCheck()) {
                // INSERT into database
                try {
                    System.out.println("Provo ad entrare nel Database");
                    Database database = new Database(2);
                    database.insertQuery("Patients", stringFields, new Object[]{name, surname, birthPlace, province, birthDate, phoneNumber, domicile, sex, taxIDCode, email, username, password, refDoc, LocalDate.now()});
                } catch (SQLException e) {
                    System.out.println("Error SQL query!");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else
            check = true;
    }

    /***
     * Checks Tax ID's string with the calculated one with all the Italian province and cities
     * @param taxIDCode String to verify
     * @return verification success
     */
    /*
    - le prime tre lettere del codice fiscale sono prese dal cognome (solitamente prima, seconda e terza consonante)
    - le seconde tre dal nome (solitamente prima, terza e quarta consonante)
    - le ultime due cifre dell'anno di nascita
    - una lettera per il mese (A = Gennaio, B, C, D, E, H, L, M, P, R, S, T = Dicembre)
    - il giorno di nascita: in caso di sesso femminile si aggiunge 40 per cui è chiaro che se si trova scritto, ad esempio, 52, non può che trattarsi di una donna nata il 12 del mese.
    - Codice del comune (quattro caratteri)
    - Carattere di controllo, per verificare la correttezza del codice fiscale.
     */
    //Check TaxIdCode
    public boolean checkTaxIdCode(String taxIDCode) {
        //Check empty fields
        if (getName().isEmpty() || getSurname().isEmpty() || getBirthDate() == null || getBirthPlace().isEmpty() || getSex() == 'Z') {
            reg.setInvalidField("taxIDCode");
            return false;
        }

        // Check if the tax code exists
        if (alreadyExist("Patients","taxIDCode", taxIDCode)){
            reg.setInvalidField("taxIDCode");
            return false;
        }

        StringBuilder codiceFiscale = new StringBuilder();
        String consonants = "bcdfghjklmnpqrstvwxyz";
        String vocals = "aeiou";
        String monthOrder = "ABCDEHLMPRST";
        String surname = getSurname().toLowerCase().replace(" ", "");   //remove space
        String name = getName().toLowerCase().replace(" ", "");
        int count = 0;

        //Check surname
        if (surname.length() == 2) //surname with only 2 characters
            //if the vowel is before the consonant
            if ((vocals.contains(String.valueOf(surname.charAt(0)))) && consonants.contains(String.valueOf(surname.charAt(1)))) {
                codiceFiscale.append(new StringBuilder(surname).reverse());
            } else
                codiceFiscale.append(surname).append('x');
        else {
            //check if there are at least 3 consonants
            for (int i = 0; i < surname.length() && count != 3; i++) {
                if (consonants.contains(String.valueOf(surname.charAt(i)))) {
                    codiceFiscale.append(surname.charAt(i)); // Prendo il carattere
                    count++;
                }
            }

            //There aren't 3 characters
            for (int i = 0; i < surname.length() && count != 3; i++) {
                if (vocals.contains(String.valueOf(surname.charAt(i)))) {
                    codiceFiscale.append(surname.charAt(i)); // Prendo la vocale
                    count++;
                }
            }
        }
        System.out.println("Codice cognome: " + codiceFiscale);

        count = 0;
        //Check name
        if (name.length() == 2)  //name with only 2 characters
            //if the vowel is before the consonant
            if ((vocals.contains(String.valueOf(name.charAt(0)))) && consonants.contains(String.valueOf(name.charAt(1)))) {
                codiceFiscale.append(new StringBuilder(name).reverse()).append("x");
            } else {
                codiceFiscale.append(name).append('x');
            }
        else {
            for (int i = 0; i < name.length() && count != 4; i++) {
                if (consonants.contains(String.valueOf(name.charAt(i)))) {
                    codiceFiscale.append(name.charAt(i)); // Prendo la consonante
                    count++;
                }
            }

            // If the name has n° consonant >= 4 remove the second one
            if (count == 4) {
                codiceFiscale.deleteCharAt(4);
            } else if (count < 3) {    // If I have less than 3 consonant
                //There aren't 3 characters
                for (int i = 0; i < name.length() && count < 3; i++) {
                    if (vocals.contains(String.valueOf(name.charAt(i)))) {
                        codiceFiscale.append(name.charAt(i));   // Prendo la vocale
                        count++;
                    }
                }
            }
        }

        // Add year value
        codiceFiscale.append(String.valueOf(birthDate.getYear()), 2, 4); //year

        // Add month code
        codiceFiscale.append(monthOrder.charAt(birthDate.getMonthValue() - 1));

        // Add days and sex
        if (sex == 'F')
            codiceFiscale.append(birthDate.getDayOfMonth() + 40);
        else {
            if(birthDate.getDayOfMonth() < 10)
                codiceFiscale.append("0");
            codiceFiscale.append(birthDate.getDayOfMonth());
        }

        // Add birthPlace
        try {
            Database codiceCatastale = new Database(1);
            ArrayList<String> codPlace = codiceCatastale.getQuery("SELECT Codice FROM CC WHERE Comune = \"" + birthPlace.toUpperCase() + "\"", new String[]{"Codice"});
            //System.out.println(codiceCatastale.getQuery("SELECT * FROM CC", new String[]{"Codice", "Comune"}));
            if(codPlace.size() > 0)
                codiceFiscale.append(codPlace.get(0));
            else {
                reg.setInvalidField("taxIDCode");
                return false;
            }
            codiceCatastale.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Compute the last character
        // 1. Get the special values first 15 characters and sum them
        int sum = 0;
        codiceFiscale = new StringBuilder(codiceFiscale.toString().toUpperCase());
        Map<Character, Integer> evenTable = Map.ofEntries(
                Map.entry('0', 0), Map.entry('1', 1), Map.entry('2', 2), Map.entry('3', 3),
                Map.entry('4', 4),Map.entry('5', 5), Map.entry('6', 6), Map.entry('7', 7),
                Map.entry('8', 8), Map.entry('9', 9), Map.entry('A', 0), Map.entry('B', 1),
                Map.entry('C', 2), Map.entry('D', 3), Map.entry('E', 4), Map.entry('F', 5),
                Map.entry('G', 6), Map.entry('H', 7), Map.entry('I', 8), Map.entry('J', 9),
                Map.entry('K', 10), Map.entry('L', 11), Map.entry('M', 12), Map.entry('N', 13),
                Map.entry('O', 14), Map.entry('P', 15), Map.entry('Q', 16), Map.entry('R', 17),
                Map.entry('S', 18), Map.entry('T', 19), Map.entry('U', 20), Map.entry('V', 21),
                Map.entry('W', 22), Map.entry('X', 23), Map.entry('Y', 24), Map.entry('Z', 25)
        );
        Map<Character, Integer> oddTable = new HashMap<>(Map.ofEntries(
                Map.entry('0', 1), Map.entry('1', 0), Map.entry('2', 5), Map.entry('3', 7),
                Map.entry('4', 9),Map.entry('5', 13), Map.entry('6', 15), Map.entry('7', 17),
                Map.entry('8', 19), Map.entry('9', 21), Map.entry('A', 1), Map.entry('B', 0),
                Map.entry('C', 5), Map.entry('D', 7), Map.entry('E', 9), Map.entry('F', 13),
                Map.entry('G', 15), Map.entry('H', 17), Map.entry('I', 19), Map.entry('J', 21),
                Map.entry('K', 2), Map.entry('L', 4), Map.entry('M', 18), Map.entry('N', 20),
                Map.entry('O', 11), Map.entry('P', 3), Map.entry('Q', 6), Map.entry('R', 8),
                Map.entry('S', 12), Map.entry('T', 14), Map.entry('U', 16), Map.entry('V', 10),
                Map.entry('W', 22), Map.entry('X', 25), Map.entry('Y', 24), Map.entry('Z', 23)
        ));

        for(int i = 0; i < codiceFiscale.toString().length(); i++) {
            if (i % 2 == 0)   // If it's odd
                sum += oddTable.get(codiceFiscale.charAt(i));
            else    // If it's even
                sum += evenTable.get(codiceFiscale.charAt(i));
        }

        // 2. Divide the obtained value by 26 and convert result in character, using a specified table
        String table = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        codiceFiscale.append(table.charAt(sum%26));

        System.out.println("Codice fiscale: " + codiceFiscale.toString().toUpperCase());

        boolean tempCheck = codiceFiscale.toString().equals(taxIDCode);

        if(!tempCheck)
            reg.setInvalidField("taxIDCode");


        return tempCheck;
    }

    /***
     * Validation check for the phone number
     * @param phoneNumber String to verify
     * @return verification success
     */
    public boolean checkPhoneNumber(String phoneNumber)
    {
        boolean tempCheck = phoneNumber.matches("^[0-9]{10,15}$") && !alreadyExist("Patients","phoneNumber", phoneNumber);

        if (!tempCheck && reg != null)
            reg.setInvalidField("phoneNumber");

        return tempCheck;
    }

    /***
     * Validation check for the email
     * @param email String to verify
     * @return verification success
     */
    public boolean checkEmail(String email) {
        boolean tempCheck = email.contains("@") && email.contains(".") && !alreadyExist("Patients","email", email);

        if (!tempCheck && reg != null)
            reg.setInvalidField("email");

        return tempCheck;
    }

    /***
     * Validation check for patient name
     * @param name String to verify if is not empty
     * @return verification success
     */
    protected boolean checkName(String name) {
        boolean tempCheck = !name.isEmpty();

        if (!tempCheck)
            reg.setInvalidField("name");

        return tempCheck;
    }

    /***
     * Validation check for patient surname
     * @param surname String to verify if is not empty
     * @return verification success
     */
    protected boolean checkSurname(String surname){
        boolean tempCheck = !surname.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("surname");

        return tempCheck;
    }

    /***
     * Validation check for username
     * @param username String to verify if is not empty and not used
     * @return verification success
     */
    public boolean checkUsername(String username) {
        // looking for Username already exist or is empty
        boolean tempCheck = !username.isEmpty() && !alreadyExist("Patients","username",username);

        if(!tempCheck && reg != null){
            reg.setInvalidField("username");
        }
        return tempCheck;
    }

    /***
     * Password verification (if matches all the requirements)
     * @param password String to verify
     * @return verification success
     */
    public boolean checkPassword(String password) {
        boolean tempCheck = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.\\-_:,;])(?=\\S+$).{8,}$");

        if (!tempCheck && reg != null)
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

    /***
     * Validation check for province
     * @param province String to verify if is not empty
     * @return verification success
     */
    // Check province
    private boolean checkProvince(String province){
        boolean tempCheck = province != null && !province.isEmpty();

        if(!tempCheck) {
            reg.setInvalidField("province");
        }

        return tempCheck;
    }

    /***
     * Validation check for birth-place
     * @param birthPlace String to verify if is not empty
     * @return verification success
     */
    private boolean checkBirthPlace(String birthPlace){
        boolean tempCheck = birthPlace != null && !birthPlace.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("birthPlace");

        return tempCheck;
    }

    /***
     * Validation check for birth-date
     * @param birthDate String to verify if is not empty
     * @return verification success
     */
    private boolean checkBirthDate(LocalDate birthDate){
        boolean tempCheck = birthDate != null && !birthDate.isAfter(LocalDate.now());

        if(!tempCheck)
            reg.setInvalidField("birthDate");

        return tempCheck;
    }

    /***
     * Validation check for domicile
     * @param domicile String to verify if is not empty
     * @return verification success
     */
    private boolean checkDomicile(String domicile){
        boolean tempCheck = domicile != null && !domicile.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("domicile");

        return tempCheck;
    }

    /***
     * Validation check for sex
     * @param sex String to verify if is not empty
     * @return verification success
     */
    private boolean checkSex(char sex) {
        boolean tempCheck = sex != 'Z';     // If no sex has been selected

        if(!tempCheck)
            reg.setInvalidField("sex");

        return tempCheck;
    }

    /***
     * Validation check for the choosen doctor
     * @param refDoc String to verify if is not empty
     * @return verification success
     */
    private boolean checkRefDoc(int refDoc){
        boolean tempCheck = refDoc != -1;   // If no doctor was selected

        if(!tempCheck)
            reg.setInvalidField("refDoc");

        return tempCheck;
    }

    /***
     *
     * @return true if all the checks are passed
     */
    public boolean getCheck() {
        return check;
    }

    /***
     * Take Birth-Place
     * @return birthPlace
     */
    public String getBirthPlace() {
        return birthPlace;
    }

    /***
     * Take Province
     * @return province
     */
    public String getProvince() {
        return province;
    }

    /***
     * Take Birth date
     * @return birthDate
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /***
     * Take Domicile
     * @return domicile
     */
    public String getDomicile() {
        return domicile;
    }

    /***
     * Take Sex
     * @return sex
     */
    public char getSex() {
        return sex;
    }

    /***
     * Take Tax ID Code
     * @return taxIDCode
     */
    public String getTaxIDCode() {
        return taxIDCode;
    }

    /***
     * Take doctor referred
     * @return refDoc
     */
    public int getRefDoc() {
        return refDoc;
    }

    /***
     * Take PatientID from the database
     * @return id
     */
    public int getPatientID() {
        try {
            Database db = new Database(2);
            int id = Integer.parseInt(db.getQuery("SELECT ID FROM Patients WHERE username = \"" + getUsername() + "\"", new String[]{"ID"}).get(0));
            db.closeAll();
            return id;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * New patient toString method
     * @return String with all data
     */
    @Override
    public String toString() {
        return "Patient:" + super.toString() + "BirthDate --> " + birthDate + "\nProvince --> " + province + "\nBithPlace --> " + birthPlace + "\nDomicile --> " + domicile + "\nSex --> " + sex + "\nTaxIDCode --> " + taxIDCode + "\nMeidoc referente --> " + refDoc + "\n";
    }

    /***
     * Set Domicile
     * @param domicile patient domicile
     */
    public void setDomicile(String domicile){this.domicile = domicile;}

    /***
     * Get patient therapy
     * @return therapy
     */
    public String getTherapy() {
        return therapy;
    }
}

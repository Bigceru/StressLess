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
    private final Registration reg;
    private boolean check;

    //Constructor
    public Patient(Registration reg, String name, String surname, String email, String phoneNumber, String username, String password, String birthPlace, String province, LocalDate birthDate, String domicile, char sex, String taxIDCode, int refDoc){
        super(reg, name, surname, email, phoneNumber, username, password);
        this.reg = reg;

        check = checkProvince(province) & checkDomicile(domicile) & checkSex(sex) & checkBirthDate(birthDate) & checkBirthPlace(birthPlace) & checkRefDoc(refDoc) & checkTaxIdCode(taxIDCode);

        if(this.getCheck()) {
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
    private boolean checkTaxIdCode(String taxIDCode) {
        if(getSurname() == null || getName() == null || birthDate == null || birthPlace == null || sex == 'Z') {
            reg.setInvalidField("taxIDCode");
            return false;
        }

        System.out.println("Sto controllando codice fiscale\n");
        StringBuilder codiceFiscale = new StringBuilder();
        String consonants = "bcdfghjklmnpqrstvwxyz";
        String vocals = "aeiou";
        String surname = getSurname().toLowerCase().replace(" ", ""); //remove space
        String name = getName().toLowerCase().replace(" ","");
        LocalDate date = birthDate;
        System.out.println("Ecco il cognome" + surname+ "\nEcco il nome" + name + "\n");
        int count = 0;

        //Check surname
        if(surname.length() == 2) //surname with only 2 characters
            //if the vowel is before the consonant
            if((vocals.contains(String.valueOf(surname.charAt(0)))) && consonants.contains(String.valueOf(surname.charAt(1)))) {
                codiceFiscale.append(new StringBuilder(surname).reverse());
            }
            else
                codiceFiscale.append(surname).append('x');
        else{
            //check if there are at least 3 consonants
            for(int i = 0; i < surname.length() && count != 3; i++){
                if(consonants.contains(String.valueOf(surname.charAt(i)))){
                    codiceFiscale.append(surname.charAt(i)); // Prendo il carattere
                    count++;
                }
            }

            //There aren't 3 characters
            for(int i = 0; i < surname.length() && count != 3; i++){
                if(vocals.contains(String.valueOf(surname.charAt(i)))){
                    codiceFiscale.append(surname.charAt(i)); // Prendo la vocale
                    count++;
                }
            }
        }
        System.out.println("Codice cognome: " + codiceFiscale);

        count = 0;
        //Check name
        if(name.length() == 2)  //name with only 2 characters
            //if the vowel is before the consonant
            if((vocals.contains(String.valueOf(name.charAt(0)))) && consonants.contains(String.valueOf(name.charAt(1)))) {
                codiceFiscale.append(new StringBuilder(name).reverse()).append("x");
            }
            else {
                codiceFiscale.append(name).append('x');
            }
        else{
            for(int i = 0; i < name.length() && count != 4; i++){
                if(consonants.contains(String.valueOf(name.charAt(i)))){
                    codiceFiscale.append(name.charAt(i)); // Prendo la consonante
                    count++;
                }
            }

            // If the name has n° consonant >= 4 remove the second one
            if(count == 4){
                codiceFiscale.deleteCharAt(4);
            }
            else if(count < 3) {    // If I have less than 3 consonant
                //There aren't 3 characters
                for (int i = 0; i < name.length() && count < 3; i++) {
                    if (vocals.contains(String.valueOf(name.charAt(i)))) {
                        codiceFiscale.append(name.charAt(i));   // Prendo la vocale
                        count++;
                    }
                }
            }
        }
        System.out.println("Codice nome: " + codiceFiscale);

        //Check date and sex
        codiceFiscale.append(String.valueOf(date.getYear()), 2, 4); //year

        boolean tempCheck = true;

        return tempCheck;
    }

    // Check province
    private boolean checkProvince(String province){
        boolean tempCheck = province != null && !province.isEmpty();

        if(!tempCheck) {
            reg.setInvalidField("province");
        }


        return tempCheck;
    }

    private boolean checkBirthPlace(String birthPlace){
        boolean tempCheck = birthPlace != null && !birthPlace.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("birthPlace");


        return tempCheck;
    }

    private boolean checkBirthDate(LocalDate birthDate){
        boolean tempCheck = birthDate != null && !birthDate.isAfter(LocalDate.now());

        if(!tempCheck)
            reg.setInvalidField("birthDate");


        return tempCheck;
    }

    private boolean checkDomicile(String domicile){
        boolean tempCheck = domicile != null && !domicile.isEmpty();

        if(!tempCheck)
            reg.setInvalidField("domicile");


        return tempCheck;
    }

    private boolean checkSex(char sex) {
        boolean tempCheck = sex != 'Z';     // If no sex has been selected

        if(!tempCheck)
            reg.setInvalidField("sex");


        return tempCheck;
    }

    private boolean checkRefDoc(int refDoc){
        boolean tempCheck = refDoc != -1;   // If no doctor was selected

        if(!tempCheck)
            reg.setInvalidField("refDoc");


        return tempCheck;
    }

    public boolean getCheck() {
        return super.getCheck() && check;
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

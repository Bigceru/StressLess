package it.univr.telemedicina;

import it.univr.telemedicina.utilities.Database;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2002, 2, 26);
        System.out.println(checkTaxIdCode("codice","DAVIDE","PORTARO", date, 'M', "Feltre"));
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
    private static boolean checkTaxIdCode(String taxIDCode, String name, String surname, LocalDate date, char sex, String birthPlace) {
        System.out.println("Sto controllando codice fiscale\n");
        StringBuilder codiceFiscale = new StringBuilder();
        String consonants = "bcdfghjklmnpqrstvwxyz";
        String vocals = "aeiou";
        String monthOrder = "ABCDEHLMPRST";
        surname = surname.toLowerCase().replace(" ", "");   //remove space
        name = name.toLowerCase().replace(" ", "");
        System.out.println("Ecco il cognome" + surname + "\nEcco il nome" + name + "\n");
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
        codiceFiscale.append(String.valueOf(date.getYear()), 2, 4); //year

        // Add month code
        codiceFiscale.append(monthOrder.charAt(date.getMonthValue() - 1));

        // Add days and sex
        if (sex == 'F')
            codiceFiscale.append(date.getDayOfMonth() + 40);
        else
            codiceFiscale.append(date.getDayOfMonth());

        // Add birthPlace
        try {
            Database codiceCatastale = new Database(1);
            ArrayList<String> codPlace = codiceCatastale.getQuery("SELECT Codice FROM CC WHERE Comune = \"" + birthPlace.toUpperCase() + "\"", new String[]{"Codice"});
            //System.out.println(codiceCatastale.getQuery("SELECT * FROM CC", new String[]{"Codice", "Comune"}));
            codiceFiscale.append(codPlace.get(0));
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

        /*if(!tempCheck)
            reg.setInvalidField("taxIDCode");
        */

        return tempCheck;
    }
}
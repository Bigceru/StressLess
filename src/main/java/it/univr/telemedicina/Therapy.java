package it.univr.telemedicina;

import it.univr.telemedicina.utilities.Database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

public class Therapy implements TherapyInterface {

    /**
     * Method to check if a therapy has been followed right to the Patient (amount of drugs and hour to take them)
     * @param idPatient id of the patient
     * @param therapyName name of the therapy
     * @param drugName name of drug taken
     * @param dailyDoses doses fo drugs taken by day
     * @param amountTaken amount of drug taken
     * @param instructions instructions of the therapy given by the doctor
     * @param startDate start date of the therapy
     * @param endDate end date of the therapy
     * @return  boolean, true if the therapy was done right, false otherwise
     */
    public boolean checkTherapy(int idPatient, String therapyName, String drugName, int dailyDoses, int amountTaken, String instructions, LocalDate startDate, LocalDate endDate){
        try {
            Database database = new Database(2);
            ArrayList<String> drugsTakenQuery = database.getQuery("SELECT Date, Hour, Quantity FROM TakenDrugs WHERE IDPatient = " + idPatient + " AND DrugName = '" + drugName + "' AND Date BETWEEN '" + startDate.toString() + "' AND '" + endDate.toString() + "'", new String[]{"Date", "Hour", "Quantity"});
            database.closeAll();

            /// If there are drugs taken by the patient during therapy period
            // "morning (06-13)","afternoon(14-18)","evening(19-00)","night(01-05)"
            // "near meals(7-9)(12-14)(19-21)", "away meals(10-11)(15-18)(22-6)"
            if(!drugsTakenQuery.isEmpty()) {
                // Add all day to drugsTaken set
                TreeMap<LocalDate, Integer> drugsTakenByDay = new TreeMap<>();
                startDate.datesUntil(endDate).forEach(localDate -> drugsTakenByDay.put(localDate, 0));

                // Cycle the drug taken and check quantity and hour
                for(int i = 0; i < drugsTakenQuery.size()-2; i += 3) {
                    LocalDate date = LocalDate.parse(drugsTakenQuery.get(i));
                    drugsTakenByDay.put(date, drugsTakenByDay.get(date) + Integer.parseInt(drugsTakenQuery.get(i+2)));  // Get drugs taken by day and add it

                    // Check the instruction and see if the hour where I took drugs was right one
                    int hour = Integer.parseInt(drugsTakenQuery.get(i+1));
                    if(instructions.contains("Vicino Pasti")) {     // close to meals
                        if(!((hour >= 7 && hour <= 9) || (hour >= 12 && hour <= 14) || (hour >= 19 && hour <= 21)))
                            return false;
                    }
                    else if(instructions.contains("Lontano Pasti")) {   // away from meals
                        if(!((hour >= 10 && hour <= 11) || (hour >= 15 && hour <= 18) || (hour >= 22 && hour <= 23) || (hour >= 0 && hour <= 6)))
                            return false;
                    }
                    else if(hour >= 6 && hour <= 13) {  // Morning
                        if(!instructions.contains("Mattina"))
                            return false;
                    }
                    else if(hour >= 14 && hour <= 18) {     // Afternoon
                        if(!instructions.contains("Pomeriggio"))
                            return false;
                    }
                    else if((hour >= 19 && hour <= 23) || hour == 0) {  // Evening
                        if(!instructions.contains("Sera"))
                            return false;
                    }
                    else if(hour >= 1 && hour <= 5) {      // Night
                        if(!instructions.contains("Notte"))
                            return false;
                    }
                }

                // Check if patient take the correct amount of drugs for each day
                for(LocalDate date : drugsTakenByDay.keySet()) {
                    // If I found a day where the patient didn't take the right amount of drugs
                    if(drugsTakenByDay.get(date) != dailyDoses * amountTaken) {
                        return false;
                    }
                }
            }
            else {  // If the Patient hasn't taken drugs
                return false;
            }
            database.closeAll();
            return true;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

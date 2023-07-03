package it.univr.telemedicina.models;

import it.univr.telemedicina.utilities.Database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Therapy implements TherapyInterface {

    private int idPatient;
    private String therapyName;
    private String drugName;
    private int dailyDoses;
    private int amountTaken;
    private String instructions;
    private LocalDate startDate;
    private LocalDate endDate;

    public Therapy(int idPatient, String therapyName, String drugName, int dailyDoses, int amountTaken, String instructions, LocalDate startDate, LocalDate endDate) {
        this.idPatient = idPatient;
        this.therapyName = therapyName;
        this.drugName = drugName;
        this.dailyDoses = dailyDoses;
        this.amountTaken = amountTaken;
        this.instructions = instructions;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void insertInDatabase() throws SQLException, ClassNotFoundException {
        // try-catch not included because it is caught by the calling class where an error message is sent
        Database database = new Database(2);

        Object[] valuesString = {this.idPatient, this.therapyName, this.drugName, this.dailyDoses, this.amountTaken, this.instructions, this.startDate, this.endDate};

        // Query for insert data in BloodPressures
        database.insertQuery("Therapies", new String[]{"IDPatient", "TherapyName", "DrugName", "DailyDoses", "AmountTaken", "Instructions", "StartDate", "EndDate"}, valuesString);
        database.closeAll();
    }

    public void removeInDatabase() throws SQLException, ClassNotFoundException {
        //try-catch not included because it is caught by the calling class where an error message is sent
        Database db = new Database(2);
        Map<String, Object> dati = new TreeMap<>();

        dati.put("IDPatient", this.idPatient);
        dati.put("TherapyName",this.therapyName);
        dati.put("DrugName", this.drugName);

        db.deleteQuery("Therapies", dati);
        db.closeAll();
    }

    public void updateInDatabase(Map<String,Object> whereValues) throws SQLException, ClassNotFoundException {
        //try-catch not included because it is caught by the calling class where an error message is sent
        Database db = new Database(2);

        // SetValues
        Map<String, Object> setDati = new TreeMap<>();
        setDati.put("IDPatient", this.idPatient);
        setDati.put("TherapyName",this.therapyName);
        setDati.put("DrugName", this.drugName);
        setDati.put("DailyDoses", this.dailyDoses);
        setDati.put("AmountTaken",this.amountTaken);
        setDati.put("Instructions", this.instructions);
        setDati.put("StartDate", this.startDate);
        setDati.put("EndDate", this.endDate);

        db.updateQuery("Therapies", setDati, whereValues);

        db.closeAll();
    }

    public int getIdPatient() {
        return idPatient;
    }

    public String getTherapyName() {
        return therapyName;
    }

    public String getDrugName() {
        return drugName;
    }

    public int getDailyDoses() {
        return dailyDoses;
    }

    public int getAmountTaken() {
        return amountTaken;
    }

    public String getInstructions() {
        return instructions;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Method to check if a therapy has been followed right to the Patient (amount of drugs and hour to take them)
     * @para idPatient id of the patient
     * @para therapyName name of the therapy
     * @para drugName name of drug taken
     * @para dailyDoses doses fo drugs taken by day
     * @para amountTaken amount of drug taken
     * @para instructions instructions of the therapy given by the doctor
     * @para startDate start date of the therapy
     * @para endDate end date of the therapy
     * @return  boolean, true if the therapy was done right, false otherwise
     */
    public boolean checkTherapy(){ //int idPatient, String therapyName, String drugName, int dailyDoses, int amountTaken, String instructions, LocalDate startDate, LocalDate endDate
        try {
            Database database = new Database(2);
            ArrayList<String> drugsTakenQuery = database.getQuery("SELECT Date, Hour, Quantity FROM TakenDrugs WHERE IDPatient = " + idPatient + " AND DrugName = '" + drugName + "' AND Date BETWEEN '" + startDate.toString() + "' AND '" + endDate.toString() + "'", new String[]{"Date", "Hour", "Quantity"});
            database.closeAll();

            System.out.println("Stampa query: " + drugsTakenQuery + " ID paziente: " + idPatient);

            /// If there are drugs taken by the patient during therapy period
            // "morning (06-13)","afternoon(14-18)","evening(19-00)","night(01-05)"
            // "near meals(7-9)(12-14)(19-21)", "away meals(10-11)(15-18)(22-6)"
            if(!drugsTakenQuery.isEmpty()) {
                // Add all day to drugsTaken set
                TreeMap<LocalDate, Integer> drugsTakenByDay = new TreeMap<>();

                // Set endDate to tomorrow if the endDate is more far
                if(LocalDate.now().plusDays(1).isBefore(endDate))
                    endDate = LocalDate.now().plusDays(1);

                startDate.datesUntil(endDate).forEach(localDate -> drugsTakenByDay.put(localDate, 0));

                // Cycle the drug taken and check quantity and hour
                for(int i = 0; i < drugsTakenQuery.size()-2; i += 3) {
                    LocalDate date = LocalDate.parse(drugsTakenQuery.get(i));

                    System.out.println("Data: " + date);

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
                        if (!instructions.contains("Pomeriggio"))
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
                        System.out.println("Figa son qui");
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

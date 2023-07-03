package it.univr.telemedicina.models;

import it.univr.telemedicina.exceptions.ParameterException;
import it.univr.telemedicina.utilities.Database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Pressure {
    private int idPatient;
    private LocalDate date;
    private LocalTime hour;
    private int systolicPressure;
    private int diastolicPressure;
    private String symptoms;
    private String conditionPressure;

    /**
     * Contructor
     * @param idPatient
     * @param date
     * @param hour
     * @param systolicPressure
     * @param diastolicPressure
     * @param symptoms
     * @param conditionPressure
     */
    public Pressure(int idPatient, LocalDate date, LocalTime hour, int systolicPressure, int diastolicPressure, String symptoms, String conditionPressure) {
        this.idPatient = idPatient;
        this.date = date;
        this.hour = hour;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.symptoms = symptoms;
        this.conditionPressure = conditionPressure;
    }

    public Pressure(int idPatient, LocalDate date, LocalTime hour, int systolicPressure, int diastolicPressure, String symptoms) {
        this.idPatient = idPatient;
        this.date = date;
        this.hour = hour;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.symptoms = symptoms;
        this.conditionPressure = checkPressure(systolicPressure, diastolicPressure);
    }

    public void insertInDatabase() throws SQLException, ClassNotFoundException {
            Database database = new Database(2);

            Object[] valuesString = {this.idPatient, this.date, this.hour + ":00", this.systolicPressure, this.diastolicPressure, this.symptoms, this.conditionPressure};

            // Query for insert data in BloodPressures
            database.insertQuery("BloodPressures", new String[]{"IDPatient", "Date", "Hour", "SystolicPressure", "DiastolicPressure", "Symptoms", "ConditionPressure"}, valuesString);

            database.closeAll();
    }

    public void removeInDatabase() throws SQLException, ClassNotFoundException {
        Database db = new Database(2);
        //Key --> fields, Values --> values
        Map<String, Object> dati = new TreeMap<>();

        dati.put("IDPatient", this.idPatient);
        dati.put("Date",this.date);
        dati.put("Hour", this.hour + ":00");
        dati.put("SystolicPressure",this.systolicPressure);
        dati.put("DiastolicPressure",this.diastolicPressure);
        //dati.put("Symptoms", this.symptoms.replace(", Altro", ""));

        db.deleteQuery("BloodPressures", dati);
        db.closeAll();
    }


    /**
     * Validation the Pressure, returning the category
     * @param systolic
     * @param diastolic
     * @return
     */
    private String checkPressure(int systolic, int diastolic){
        ArrayList<String> category = new ArrayList<>(Arrays.asList("Ottimale","Normale","Normale - alta","Ipertensione di Grado 1 borderline","Ipertensione di Grado 1 lieve","Ipertensione di Grado 2 moderata", "Ipertensione di Grado 3 grave", "Ipertensione sistolica isolata borderline", "Ipertensione sistolica isolata"));
        ArrayList<Integer> valuesSystolic = new ArrayList<>(Arrays.asList(120, 130, 139, 149, 159, 179, 180, 250));    //140-149,>=150
        ArrayList<Integer> valuesDiastolic = new ArrayList<>(Arrays.asList(0, 80, 85, 89, 94, 99, 109, 110));    //<90
        int index = -1;

        //Check systolic
        for(Integer value : valuesSystolic){
            if(value >= systolic) {
                index = valuesSystolic.indexOf(value);
                //Special case
                if(index == 3 && diastolic < 90)
                    return category.get(7);
                if(index > 3 && diastolic < 90)
                    return category.get(8);
                break;
            }
        }

        //Parto direttamente dall'index
        //check diastolic
        for(int i = index; i < valuesDiastolic.size(); i++)
        {
            if(valuesDiastolic.get(i) >= diastolic){
                return category.get(i);
            }
        }

        // Example systolic = 300
        return "Valori fuori norma";
    }

    /**
     * Validation of the following parameters
     * @param systolic
     * @param diastolic
     * @param datePress
     * @param time
     */
    public static void checkPressuresParameters(int systolic, int diastolic, LocalDate datePress, String time) {
        // check diastolic
        if (diastolic <= 0 || diastolic >= 150)
            throw new ParameterException("Pressione diastolica error");

        // check systolic
        if (systolic <= 0 || systolic >= 250)
            throw new ParameterException("Sistolica errore");

        if (systolic <= diastolic)
            throw new ParameterException("Confronto errore");

        // check if the mensuration date is right
        if (datePress.isAfter(LocalDate.now()))
            throw new ParameterException("Data errore");
        if(datePress.isEqual(LocalDate.now())){
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < Integer.parseInt(time)){
                throw new ParameterException("Ora errore");
            }
        }
        if(time.isEmpty())
            throw new ParameterException("Orario errore");
    }

    /**
     * Method to get ID of the patient
     * @return Id
     */
    public int getIdPatient() {
        return this.idPatient;
    }

    /**
     * Method to get the Condition Pressure
     * @return Condition
     */
    public String getConditionPressure(){
        return this.conditionPressure;
    }

    public LocalDate getDate(){
        return this.date;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public LocalTime getHour() {
        return hour;
    }

    public String getSymptoms() {
        return symptoms;
    }
}

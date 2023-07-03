package it.univr.telemedicina.models;

import it.univr.telemedicina.utilities.Database;
import it.univr.telemedicina.utilities.TherapyFields;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TherapyList extends ArrayList<Therapy> {

    /**
     * Constructor to add all therapies
     */
    public TherapyList(){
        try{
            Database db = new Database(2);
            ArrayList<String> resultQuery = db.getQuery("SELECT * FROM Therapies", new String[]{"IDPatient","TherapyName", "DrugName", "DailyDoses", "AmountTaken", "Instructions", "StartDate", "EndDate"});

            // Cycle the query results and add all the therapies to the list
            for(int i = 0; i < resultQuery.size() - 7; i += 8){
                this.add(new Therapy(Integer.parseInt(resultQuery.get(i)),resultQuery.get(i+1),resultQuery.get(i+2),Integer.parseInt(resultQuery.get(i+3)),Integer.parseInt(resultQuery.get(i+4)),resultQuery.get(i+5), LocalDate.parse(resultQuery.get(i+6)), LocalDate.parse(resultQuery.get(i+7))));
            }

            db.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that return all therapy by ID
     * @param idPatient id of patient that i want
     * @return list of therapies
     */
    public ArrayList<Therapy> getTherapyById(int idPatient) {
        ArrayList<Therapy> list = new ArrayList<>();

        // Cycle all the pressure and take only the ones with idPatient = id
        for(Therapy therapy : this) {
            if(therapy.getIdPatient() == idPatient)
                list.add(therapy);
        }

        return list;
    }

    /**
     * Method that return all pressure by dates
     * @param idPatient id of patient that i want
     * @param start start date
     * @param end end date
     * @return
     */
    public ArrayList<Therapy> getTherapyByDate(int idPatient,LocalDate start, LocalDate end) {
        // Take all pressure by ID
        ArrayList<Therapy> list = getTherapyById(idPatient);
        ArrayList<Therapy> correctTherapy = new ArrayList<>();

        // Cycle all the therapies found by id and get only the ones with the date in the range
        for(Therapy therapy : list) {
            if(therapy.getStartDate().isAfter(start) || therapy.getStartDate().isEqual(start))
                if (end == null || therapy.getEndDate().isBefore(end) || therapy.getEndDate().isEqual(end))
                    correctTherapy.add(therapy);
        }
        System.out.println("SIZE " + correctTherapy.size());
        correctTherapy.forEach(therapy -> System.out.println(therapy.getTherapyName()));

        return correctTherapy;
    }

    /**
     * Method that return ongoing therapies
     * @param idPatient id of patient that i want
     * @return
     */
    public ArrayList<Therapy> getCurrentTherapy(int idPatient) {
        ArrayList<Therapy> list = getTherapyById(idPatient);
        ArrayList<Therapy> correctTherapy = new ArrayList<>();

        // Cycle all the therapies found by id and get only the ones witch are currently in use
        for(Therapy therapy : list) {
            if(LocalDate.now().compareTo(therapy.getStartDate()) >= 0 && LocalDate.now().compareTo(therapy.getEndDate()) <= 0)
                correctTherapy.add(therapy);
        }

        return correctTherapy;
    }

    /**
     * Method that transform a therapiesList in a arrayList of String
     * @param therapiesList List of therapies to wrap in String
     * @return arraylist<String>
     */
    public ArrayList<String> getTherapyToString(ArrayList<Therapy> therapiesList){
        ArrayList<String> listString = new ArrayList<>(); //IDPatient","TherapyName", "DrugName", "DailyDoses", "AmountTaken", "Instructions", "StartDate", "EndDate"
        for(Therapy therapy : therapiesList){
            listString.add(String.valueOf(therapy.getIdPatient()));
            listString.add(therapy.getTherapyName());
            listString.add(therapy.getDrugName());
            listString.add(String.valueOf(therapy.getDailyDoses()));
            listString.add(String.valueOf(therapy.getAmountTaken()));
            listString.add(therapy.getInstructions());
            listString.add(therapy.getStartDate().toString());
            listString.add(therapy.getEndDate().toString());
        }

        return listString;
    }

    /**
     * Method to select data from an array
     * @param therapies array that i extract only a several part of the data
     * @param choice which data i want
     * @return list that contain the elements based choice
     */
    public ArrayList<String> getWhatUWantString(ArrayList<String> therapies, TherapyFields[] choice){
        ArrayList<String> list = new ArrayList<>();
        int size = therapies.size();

        // Cycle all pressures and select based choice
        for(int i = 0; i < size - 7; i += 8){
            // Slide choice
            for(TherapyFields j : choice)
                list.add(therapies.get(i + j.ordinal())); // Add element that i want
        }

        return list;
    }
}

package it.univr.telemedicina.models;

import it.univr.telemedicina.utilities.Database;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class PressureList extends ArrayList<Pressure> {

    /**
     * Constructor to add all pressure
     */
    public PressureList() {
        try {
            Database database = new Database(2);

            ArrayList<String> resultQuery = database.getQuery("SELECT * FROM BloodPressures", new String[]{"IDPatient","Date","Hour","SystolicPressure", "DiastolicPressure","Symptoms", "ConditionPressure"});

            // Cycle the query results and add all the pressures to the list
            for(int i = 0; i < resultQuery.size()-8; i += 7){
                this.add(new Pressure(Integer.parseInt(resultQuery.get(i)), LocalDate.parse(resultQuery.get(i+1)), LocalTime.parse(resultQuery.get(i+2)), Integer.parseInt(resultQuery.get(i+3)), Integer.parseInt(resultQuery.get(i+4)), resultQuery.get(i+5), resultQuery.get(i+6)));
            }

            database.closeAll();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that return all pressure by ID
     * @param idPatient id of patient that i want
     * @return list of pressure
     */
    public ArrayList<Pressure> getPressuresById(int idPatient) {
        ArrayList<Pressure> list = new ArrayList<>();

        // Cycle all the pressure and take only the ones with idPatient = id
        for(Pressure pressure : this) {
            if(pressure.getIdPatient() == idPatient)
                list.add(pressure);
        }

        return list;
    }

    /**
     * Method that return all pressure by conditionPressure
     * @param conditionPressure a string that contain 1 or more conditionPressure
     * @return list of pressure
     */
    public ArrayList<Pressure> getPressuresByCondition(String conditionPressure) {
        ArrayList<Pressure> list = new ArrayList<>();

        // Cycle all the pressure and take only the ones with conditionPressure contained in the conditionPressure parameter
        for(Pressure pressure : this) {
            if(conditionPressure.contains(pressure.getConditionPressure()))
                list.add(pressure);
        }

        return list;
    }

    /**
     * Return the last pressure of that patient
     * @param idPatient id of patient that i want
     * @return last pressure
     */
    public Pressure getLastPressure(int idPatient){
        // Create TreeSet with comparator by Date
        TreeSet<Pressure> pressureTreeSet = new TreeSet<>(Comparator.comparing(Pressure::getDate));
        pressureTreeSet.addAll(getPressuresById(idPatient));
        return pressureTreeSet.last();  // Get last date
    }

    /**
     * Method that return all pressure by dates
     * @param idPatient id of patient that i want
     * @param start start date
     * @param end end date
     * @return
     */
    public ArrayList<Pressure> getPressuresByDate(int idPatient,LocalDate start, LocalDate end){
        // Take all pressure by ID
        ArrayList<Pressure> list = getPressuresById(idPatient);

        // Filter by dates
        for(int i = list.size() - 1; i >= 0; i--){
            // Save date
            LocalDate dateTmp = list.get(i).getDate();

            // Check if the date is NOT included between start-end
            if(!((dateTmp.isAfter(start) && dateTmp.isBefore(end)) || dateTmp.isEqual(start) || dateTmp.isEqual(end))){
                list.remove(i); // Remove
            }
        }

        return list;
    }
}

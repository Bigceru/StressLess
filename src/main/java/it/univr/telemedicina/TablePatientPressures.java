package it.univr.telemedicina;

import java.time.LocalDate;

public class TablePatientPressures {
    private LocalDate date;
    private String hour;
    private String pressSD;
    private String state;
    private String symptomps;

    /**
     * Constructor for table in Patient home scene
     * @param date date of pressure taken
     * @param hour hour of pressure taken
     * @param pressSystolic date of pressure taken
     * @param pressDiastolic date of pressure taken
     * @param state date of pressure taken
     */
    public TablePatientPressures(LocalDate date, String hour, int pressSystolic, int pressDiastolic, String state){
        this.date = date;
        this.pressSD = pressSystolic + "/" + pressDiastolic;
        this.hour = hour;
        this.state = state;
    }

    /**
     * Constructor for table in Doctor Search Patient scene
     * @param date Date of registered pressure
     * @param hour Hour of registered pressure
     * @param symptoms Patient symptoms with taken pressure
     * @param pressSystolic systolic of pressure taken
     * @param pressDiastolic diastolic of pressure taken
     * @param state state (low, high, ...) of pressure taken
     */
    public TablePatientPressures(LocalDate date, String hour, int pressSystolic, int pressDiastolic, String symptoms, String state){
        this.date = date;
        this.pressSD = pressSystolic + "/" + pressDiastolic;
        this.state = state;
        this.symptomps = symptoms;
        this.hour = hour;
    }

    /**
     *
     * @return localDate
     */
    public LocalDate getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

    public String getPressSD(){return pressSD;}

    public String getHour() {
        return hour.split(":")[0];
    }
    public String getSymptomps(){return symptomps;}
}

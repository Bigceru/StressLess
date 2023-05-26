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
     * @param date
     * @param hour
     * @param pressSystolic
     * @param pressDiastolic
     * @param state
     */
    public TablePatientPressures(LocalDate date, String hour, int pressSystolic, int pressDiastolic, String state){
        this.date = date;
        this.pressSD = pressSystolic + "/" + pressDiastolic;
        this.hour = hour;
        this.state = state;
    }


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
}

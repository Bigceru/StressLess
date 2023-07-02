package it.univr.telemedicina;

import java.time.LocalDate;

public class DrugsTablePat {
    private String drugName;
    private LocalDate date;
    private String amount;
    private String hour;

    /**
     * Drugs Patient table entry
     * @param date Taken drug date
     * @param hour Taken drug hour
     * @param drugName Taken drug name
     * @param amount Taken drug amount
     */
    public DrugsTablePat(LocalDate date, String hour , String drugName, String amount){
        this.drugName = drugName;
        this.date = date;
        this.amount = amount;
        this.hour = hour;
    }

    /**
     * Get drug name
     * @return drugName
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * Get assumption date
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Get amount
     * @return amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Get drug name
     * @return drugName
     */
    public String getHour() {return hour.split(":")[0];}
}

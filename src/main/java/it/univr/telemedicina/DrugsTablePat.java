package it.univr.telemedicina;

import java.time.LocalDate;

public class DrugsTablePat {
    private String drugName;
    private LocalDate date;
    private String amount;
    private String hour;

    public DrugsTablePat(LocalDate date, String hour , String drugName, String amount){
        this.drugName = drugName;
        this.date = date;
        this.amount = amount;
        this.hour = hour;
    }


    public String getDrugName() {
        return drugName;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getHour() {return hour.split(":")[0];}
}

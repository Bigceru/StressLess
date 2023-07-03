package it.univr.telemedicina.models;

import java.time.LocalDate;

public interface TherapyInterface {
    boolean checkTherapy(); //int idPatient, String therapyName, String drugName, int dailyDoses, int amountTaken, String instructions, LocalDate startDate, LocalDate endDate
}

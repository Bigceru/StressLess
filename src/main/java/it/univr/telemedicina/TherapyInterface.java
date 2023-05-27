package it.univr.telemedicina;

import java.time.LocalDate;

public interface TherapyInterface {
    public boolean checkTherapy(int idPatient, String therapyName, String drugName, int dailyDoses, int amountTaken, String instructions, LocalDate startDate, LocalDate endDate);
}

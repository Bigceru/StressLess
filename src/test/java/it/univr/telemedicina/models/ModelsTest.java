package it.univr.telemedicina.models;

import it.univr.telemedicina.exceptions.ParameterException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

class ModelsTest {

    @Test
    void checkPressuresParameters() {
        Pressure pressure = new Pressure(6, LocalDate.now(), LocalTime.now(), 120, 60, "Ansia");

        assertThrows(ParameterException.class, ()-> Pressure.checkPressuresParameters(pressure.getSystolicPressure(), pressure.getDiastolicPressure(), pressure.getDate(), pressure.getHour().toString()));
    }
}
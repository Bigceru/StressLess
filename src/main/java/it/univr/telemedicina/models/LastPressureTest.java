package it.univr.telemedicina.models;

import org.testng.annotations.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class LastPressureTest {

    @Test
    void getLastPressure() {
        PressureList list = new PressureList();
        assertEquals(new Pressure(6, LocalDate.parse("2023-07-01"), LocalTime.parse("15:00:00"), 120, 80, "Ansia", "Normale"), list.getLastPressure(6));
    }
}
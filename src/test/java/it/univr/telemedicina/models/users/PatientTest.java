package it.univr.telemedicina.models.users;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    Patient p = new Patient(null, "alessia", "de benedittis", "pazzoini@gmail.com", "3417029110","ilPazzo", "PAzzio23", "bari", "ba", LocalDate.parse("2001-08-01"), "Via piazza flavio", 'F',"DBNLSS01M41A662P" , 2);
    @Test
    void getTestPatient() {
        assertEquals("Giacomo", p.getName());
        assertEquals("Pazzini", p.getSurname());
        assertEquals("pazzoini@gmail.com", p.getEmail());
        assertEquals("3417029110", p.getPhoneNumber());
        assertEquals("ilPazzo", p.getUsername());

    }

    @Test
    void testTaxIDCode() {
        assertEquals(true, p.checkTaxIdCode(p.getTaxIDCode()));

    }
    @Test
    void testEmail(){
        assertEquals(true, p.checkEmail(p.getEmail()));
    }

    Doctor doctor = new Doctor();

    // DOCTOR TEST


}
package com.university;

import com.university.facade.RegistrationFacade;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationFacadeTest {
    @BeforeEach
    void clean() throws Exception {
        Path d=Path.of("data");
        if(Files.exists(d)) try(var s=Files.walk(d)){s.sorted(java.util.Comparator.reverseOrder()).forEach(p->{try{Files.delete(p);}catch(Exception ignored){}});}
    }

    @Test
    void successfulRegistration() {
        RegistrationFacade f=new RegistrationFacade();
        assertTrue(f.register("S1","Java",true,false,false).startsWith("SUCCESS"));
    }

    @Test
    void bFailureCompensatesA() {
        RegistrationFacade f=new RegistrationFacade();
        String result=f.register("S2","Java",true,true,false);
        assertTrue(result.contains("B FAILURE"));
        assertTrue(Files.exists(Path.of("data/a_checks.csv")));
        assertTrue(Files.readString(Path.of("data/a_checks.csv")).contains("CANCELLED"));
    }

    @Test
    void cFailureCompensatesBThenA() {
        RegistrationFacade f=new RegistrationFacade();
        String result=f.register("S3","Advanced Java",true,false,true);
        assertTrue(result.contains("C FAILURE"));
        assertTrue(Files.readString(Path.of("data/a_checks.csv")).contains("CANCELLED"));
        assertTrue(Files.readString(Path.of("data/b_allocations.csv")).contains("CANCELLED"));
    }

    @Test
    void invalidInputFailsBeforeACommit() {
        RegistrationFacade f=new RegistrationFacade();
        assertThrows(IllegalArgumentException.class, () -> f.register("", "Java", true, false, false));
    }
}
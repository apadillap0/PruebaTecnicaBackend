package com.alianza.pruebatecnica.core.manager;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogImplTest {

    private LogImpl logTest;

    @BeforeAll
    void setUpBeforeClass() throws Exception {
        logTest = new LogImpl();
    }

    @Test
    void testInfoString() {
        logTest.info("prueba");
        assertTrue(true);
    }

    @Test
    void testInfoStringString() {
        logTest.info("prueba", "prueba");
        assertTrue(true);
    }

    @Test
    void testError() {
        logTest.error("error");
        assertTrue(true);
    }

    @Test
    void testWarm() {
        logTest.warm("prueba");
        assertTrue(true);
    }
}

package com.hms.api.patient.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Year;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.patient.repository.PatientRepository;

import com.hms.patient.service.PatientNumberGeneratorImpl;

@ExtendWith(MockitoExtension.class)
class PatientNumberGeneratorTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientNumberGeneratorImpl generator;

    private int currentYear;

    @BeforeEach
    void setup() {

        currentYear = Year.now().getValue();
    }
    
    @Test
    void shouldGeneratePatientNumber() {

        when(repository.nextPatientSequence())
                .thenReturn(1L);

        String patientNumber =
                generator.generate();

        assertNotNull(patientNumber);

        verify(repository)
                .nextPatientSequence();
    }
    
    @Test
    void shouldPrefixWithHMS() {

        when(repository.nextPatientSequence())
                .thenReturn(5L);

        String patientNumber =
                generator.generate();

        assertTrue(

                patientNumber.startsWith("HMS-"));

    }
    
    @Test
    void shouldContainCurrentYear() {

        when(repository.nextPatientSequence())
                .thenReturn(7L);

        String patientNumber =
                generator.generate();

        assertTrue(

                patientNumber.contains(

                        String.valueOf(currentYear)));

    }
    
    @Test
    void shouldIncrementSequence() {

        when(repository.nextPatientSequence())

                .thenReturn(1L)

                .thenReturn(2L);

        String first =
                generator.generate();

        String second =
                generator.generate();

        assertNotEquals(

                first,

                second);

        verify(repository, times(2))

                .nextPatientSequence();
    }
    
    @Test
    void shouldFormatSixDigits() {

        when(repository.nextPatientSequence())
                .thenReturn(25L);

        String patientNumber =
                generator.generate();

        assertEquals(

                "HMS-" + currentYear + "-000025",

                patientNumber);

    }
    
    @Test
    void shouldFormatLargeSequence() {

        when(repository.nextPatientSequence())
                .thenReturn(123456L);

        String patientNumber =
                generator.generate();

        assertEquals(

                "HMS-" + currentYear + "-123456",

                patientNumber);

    }
    
    @Test
    void shouldGenerateUniqueNumbers() {

        when(repository.nextPatientSequence())

                .thenReturn(100L)

                .thenReturn(101L)

                .thenReturn(102L);

        String first =
                generator.generate();

        String second =
                generator.generate();

        String third =
                generator.generate();

        assertNotEquals(first, second);

        assertNotEquals(second, third);

        assertNotEquals(first, third);
    }
    
    @Test
    void shouldCallRepositoryOncePerGeneration() {

        when(repository.nextPatientSequence())
                .thenReturn(1L);

        generator.generate();

        verify(repository, times(1))
                .nextPatientSequence();

        verifyNoMoreInteractions(repository);
    }
    
    
    @Test
    void shouldSupportSequenceAboveOneMillion() {

        when(repository.nextPatientSequence())
                .thenReturn(1_000_001L);

        String patientNumber =
                generator.generate();

        assertEquals(

                "HMS-" + currentYear + "-1000001",

                patientNumber);

    }
    
    @Test
    void shouldNeverReturnBlank() {

        when(repository.nextPatientSequence())
                .thenReturn(55L);

        String patientNumber =
                generator.generate();

        assertFalse(patientNumber.isBlank());

    }
    
    @Test
    void shouldRejectInvalidSequence() {

        when(repository.nextPatientSequence())
                .thenReturn(0L);

        assertThrows(

                IllegalStateException.class,

                () -> generator.generate());

    }
    

}

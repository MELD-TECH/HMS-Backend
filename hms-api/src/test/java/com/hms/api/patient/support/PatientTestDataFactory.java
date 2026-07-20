package com.hms.api.patient.support;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hms.patient.dto.request.ActivatePatientRequest;
import com.hms.patient.dto.request.ArchivePatientRequest;
import com.hms.patient.dto.request.CreatePatientRequest;
import com.hms.patient.dto.request.DeceasedPatientRequest;
import com.hms.patient.dto.request.UpdatePatientRequest;
import com.hms.patient.entity.Patient;
import com.hms.patient.enums.BloodGroup;
import com.hms.patient.enums.Gender;
import com.hms.patient.enums.Genotype;
import com.hms.patient.enums.MaritalStatus;
import com.hms.patient.enums.PatientStatus;
import com.hms.patient.repository.PatientRepository;

public final class PatientTestDataFactory {

    private PatientTestDataFactory() {
    }

    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_MIDDLE_NAME = "Peter";
    public static final String DEFAULT_LAST_NAME = "Doe";

    public static Patient activePatient() {

        return Patient.builder()

                .patientNumber(UniqueTestData.patientNumber())

                .firstName(DEFAULT_FIRST_NAME)

                .middleName(DEFAULT_MIDDLE_NAME)

                .lastName(DEFAULT_LAST_NAME)

                .dateOfBirth(LocalDate.of(1990, 1, 1))

                .gender(Gender.MALE)

                .maritalStatus(MaritalStatus.SINGLE)

                .bloodGroup(BloodGroup.O_POSITIVE)

                .genotype(Genotype.AA)

                .email(UniqueTestData.email())

                .phoneNumber(UniqueTestData.phoneNumber())

                .status(PatientStatus.ACTIVE)

                .deceased(false)

                .build();
    }
    
    public static Patient archivedPatient() {

        Patient patient = activePatient();

        patient.setFirstName("Michael");
        patient.setMiddleName("James");
        patient.setLastName("Brown");

        patient.setStatus(PatientStatus.ARCHIVED);

        patient.setStatus(PatientStatus.ARCHIVED);

        patient.setArchiveReason("Duplicate registration");

        patient.setArchivedBy("admin");

        patient.setArchivedAt(LocalDateTime.now());

        return patient;
    }
    
    
    public static Patient inactivePatient() {

        Patient patient = activePatient();
        
        patient.setFirstName("Sarah");
        patient.setMiddleName("Grace");
        patient.setLastName("Johnson");

        patient.setStatus(PatientStatus.INACTIVE);

        return patient;
    }
    
    public static Patient deceasedPatient() {

        Patient patient = activePatient();
        
        patient.setFirstName("David");
        patient.setMiddleName("Paul");
        patient.setLastName("Wilson");

        patient.setStatus(PatientStatus.DECEASED);

        patient.setDeceased(true);

        patient.setDeceasedDate(LocalDate.now());

        patient.setCauseOfDeath("Cardiac Arrest");

        patient.setDeceasedNotes("Certified by attending physician");

        return patient;
    }
    
    public static CreatePatientRequest validCreateRequest() {

        return CreatePatientRequest.builder()

                .firstName("John")

                .middleName("Peter")

                .lastName("Doe")

                .dateOfBirth(LocalDate.of(1990,1,1))

                .gender(Gender.MALE)

                .maritalStatus(MaritalStatus.SINGLE)

                .bloodGroup(BloodGroup.O_POSITIVE)

                .genotype(Genotype.AA)

                .email(UniqueTestData.email())

                .phoneNumber(UniqueTestData.phoneNumber())

                .build();
    }
    
    
    public static UpdatePatientRequest
    validUpdateRequest() {

        return UpdatePatientRequest.builder()

                .firstName("Johnny")

                .middleName("Paul")

                .lastName("Doe")

                .email(
                        UniqueTestData.email())

                .phoneNumber(
                        UniqueTestData.phoneNumber())

                .dateOfBirth(
                        LocalDate.of(1992,5,20))

                .gender(Gender.MALE)

                .maritalStatus(
                        MaritalStatus.MARRIED)

                .bloodGroup(
                        BloodGroup.A_POSITIVE)

                .genotype(
                        Genotype.AS)

                .build();
    }
    
    public static Patient secondPatient() {

        Patient patient = activePatient();

        patient.setPatientNumber(UniqueTestData.patientNumber());

        patient.setFirstName("Jane");

        patient.setMiddleName("Mary");

        patient.setLastName("Smith");

        patient.setEmail(UniqueTestData.email());

        patient.setPhoneNumber(UniqueTestData.phoneNumber());

        patient.setGender(Gender.FEMALE);

        return patient;
    }
   
    public static Patient activePatient(String firstName, String email) {

        Patient patient = activePatient();

        patient.setFirstName(firstName);

        patient.setEmail(email);

        return patient;
    }
    
    public static Patient saveActivePatient(
            PatientRepository repository) {

        return repository.save(activePatient());
    }
    
    public static Patient saveArchivedPatient(
            PatientRepository repository) {

        return repository.save(archivedPatient());
    }

    public static Patient saveInactivePatient(
            PatientRepository repository) {

        return repository.save(inactivePatient());
    }

    public static Patient saveDeceasedPatient(
            PatientRepository repository) {

        return repository.save(deceasedPatient());
    }
    
    public static UpdatePatientRequest blankFirstNameRequest() {

        UpdatePatientRequest request = validUpdateRequest();

        request.setFirstName("");

        return request;
    }
    
    public static UpdatePatientRequest blankLastNameRequest() {

        UpdatePatientRequest request = validUpdateRequest();

        request.setLastName("");

        return request;
    }
    
    public static UpdatePatientRequest invalidEmailRequest() {

        UpdatePatientRequest request = validUpdateRequest();

        request.setEmail("invalid-email");

        return request;
    }
    
    public static UpdatePatientRequest invalidPhoneRequest() {

        UpdatePatientRequest request = validUpdateRequest();

        request.setPhoneNumber("123");

        return request;
    }
    
    public static UpdatePatientRequest futureDobRequest() {

        UpdatePatientRequest request = validUpdateRequest();

        request.setDateOfBirth(LocalDate.now().plusDays(1));

        return request;
    }
    
	public static UpdatePatientRequest duplicateEmailRequest(String email) {

	    UpdatePatientRequest request = validUpdateRequest();

	    request.setEmail(email);

	    return request;
	}
	
	
	
	public static UpdatePatientRequest duplicatePhoneRequest(String phone) {

	    UpdatePatientRequest request = validUpdateRequest();

	    request.setPhoneNumber(phone);

	    return request;
	}
	
	public static ArchivePatientRequest
	validArchiveRequest() {

	    return ArchivePatientRequest.builder()

	            .reason("Duplicate patient registration.")

	            .build();
	}
	
	public static ArchivePatientRequest
	blankArchiveRequest() {

	    ArchivePatientRequest request =
	            validArchiveRequest();

	    request.setReason("");

	    return request;
	}
	
	public static ArchivePatientRequest
	nullArchiveRequest() {

	    ArchivePatientRequest request =
	            validArchiveRequest();

	    request.setReason(null);

	    return request;
	}
	
	public static ArchivePatientRequest
	shortArchiveRequest() {

	    ArchivePatientRequest request =
	            validArchiveRequest();

	    request.setReason("abc");

	    return request;
	}
	
	public static ArchivePatientRequest
	longArchiveRequest() {

	    ArchivePatientRequest request =
	            validArchiveRequest();

	    request.setReason("A".repeat(501));

	    return request;
	}
	
	public static ArchivePatientRequest
	archiveRequest(String reason) {

	    return ArchivePatientRequest.builder()

	            .reason(reason)

	            .build();
	}
	
	public static ActivatePatientRequest validActivateRequest() {

	    return ActivatePatientRequest.builder()
	            .reason("Patient records successfully reviewed and reactivated.")
	            .build();
	}
	
	public static ActivatePatientRequest blankActivateRequest() {

	    return ActivatePatientRequest.builder()
	            .reason("")
	            .build();
	}
	
	public static ActivatePatientRequest nullActivateRequest() {

	    return ActivatePatientRequest.builder()
	            .reason(null)
	            .build();
	}
	
	public static ActivatePatientRequest shortActivateRequest() {

	    return ActivatePatientRequest.builder()
	            .reason("abc")
	            .build();
	}
	
	public static ActivatePatientRequest longActivateRequest() {

	    return ActivatePatientRequest.builder()
	            .reason("A".repeat(501))
	            .build();
	}
	
	public static ActivatePatientRequest activateRequest(
	        String reason) {

	    return ActivatePatientRequest.builder()
	            .reason(reason)
	            .build();
	}	

	public static DeceasedPatientRequest validDeceasedRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("Cardiac arrest")

	            .deceasedNotes("Death confirmed by attending physician.")

	            .build();
	}
	
	public static DeceasedPatientRequest blankCauseOfDeathRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("")

	            .deceasedNotes("Death confirmed.")

	            .build();
	}
	
	public static DeceasedPatientRequest nullCauseOfDeathRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath(null)

	            .deceasedNotes("Death confirmed.")

	            .build();
	}
	
	public static DeceasedPatientRequest futureDateOfDeathRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now().plusDays(1))

	            .causeOfDeath("Cardiac arrest")

	            .deceasedNotes("Death confirmed.")

	            .build();
	}
	
	public static DeceasedPatientRequest blankReasonRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("Cardiac arrest")

	            .deceasedNotes("")

	            .build();
	}
	
	public static DeceasedPatientRequest nullReasonRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("Cardiac arrest")

	            .deceasedNotes(null)

	            .build();
	}
	
	public static DeceasedPatientRequest longCauseOfDeathRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("A".repeat(501))

	            .deceasedNotes("Death confirmed.")

	            .build();
	}
	
	public static DeceasedPatientRequest longReasonRequest() {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(LocalDate.now())

	            .causeOfDeath("Cardiac arrest")

	            .deceasedNotes("A".repeat(501))

	            .build();
	}
	
	public static DeceasedPatientRequest deceasedRequest(

	        LocalDate dateOfDeath,

	        String causeOfDeath,

	        String reason) {

	    return DeceasedPatientRequest.builder()

	            .deceasedDate(dateOfDeath)

	            .causeOfDeath(causeOfDeath)

	            .deceasedNotes(reason)

	            .build();
	}
}
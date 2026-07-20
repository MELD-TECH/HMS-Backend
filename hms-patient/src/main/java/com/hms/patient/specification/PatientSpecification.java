package com.hms.patient.specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.hms.patient.dto.request.SearchPatientRequest;
import com.hms.patient.entity.Patient;

public final class PatientSpecification {

    private PatientSpecification() {
    }

    private String keyword;
    
    public static Specification<Patient> search(
            SearchPatientRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                return cb.conjunction();
            }

            if (StringUtils.hasText(request.getPatientNumber())) {

                predicates.add(

                        cb.equal(

                                root.get("patientNumber"),

                                request.getPatientNumber()));
            }

            if (StringUtils.hasText(request.getFirstName())) {

                predicates.add(

                        cb.like(

                                cb.lower(root.get("firstName")),

                                "%" + request.getFirstName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(request.getLastName())) {

                predicates.add(

                        cb.like(

                                cb.lower(root.get("lastName")),

                                "%" + request.getLastName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(request.getPhoneNumber())) {

                predicates.add(

                        cb.equal(

                                root.get("phoneNumber"),

                                request.getPhoneNumber()));
            }

            if (StringUtils.hasText(request.getEmail())) {

                predicates.add(

                        cb.equal(

                                cb.lower(root.get("email")),

                                request.getEmail().toLowerCase()));
            }

            if (request.getGender() != null) {

                predicates.add(

                        cb.equal(

                                root.get("gender"),

                                request.getGender()));
            }

            if (request.getStatus() != null) {

                predicates.add(

                        cb.equal(

                                root.get("status"),

                                request.getStatus()));
            }

            if (StringUtils.hasText(request.getKeyword())) {

                String value =
                        "%" + request.getKeyword().toLowerCase() + "%";

                predicates.add(

                        cb.or(

                                cb.like(cb.lower(root.get("firstName")), value),

                                cb.like(cb.lower(root.get("middleName")), value),

                                cb.like(cb.lower(root.get("lastName")), value),

                                cb.like(cb.lower(root.get("patientNumber")), value),

                                cb.like(cb.lower(root.get("phoneNumber")), value),

                                cb.like(cb.lower(root.get("email")), value)
                        ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
package com.hms.api.patient.support;


import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class UniqueTestData {

    private static final AtomicInteger SEQUENCE =
            new AtomicInteger(1);

    private UniqueTestData() {
    }

    public static String patientNumber() {

        return String.format(
                "PAT%08d",
                SEQUENCE.getAndIncrement());
    }

    public static String email() {

        return UUID.randomUUID()

                + "@test.com";
    }

    public static String phoneNumber() {

        return "080"

                + String.format(
                        "%08d",
                        SEQUENCE.getAndIncrement());
    }
}

CREATE TABLE patient_schema.patients (

    id UUID PRIMARY KEY,

    patient_number VARCHAR(30) NOT NULL UNIQUE,

    first_name VARCHAR(80) NOT NULL,

    middle_name VARCHAR(80),

    last_name VARCHAR(80) NOT NULL,

    date_of_birth DATE NOT NULL,

    gender VARCHAR(20) NOT NULL,

    marital_status VARCHAR(20) NOT NULL,

    blood_group VARCHAR(20) NOT NULL,

    genotype VARCHAR(20) NOT NULL,

    email VARCHAR(120),

    phone_number VARCHAR(30),

    deceased BOOLEAN NOT NULL DEFAULT FALSE,

    deceased_date DATE,

    status VARCHAR(20) NOT NULL,

    profile_photo_id UUID,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),
    
    full_name VARCHAR(150) NOT NULL,

    version BIGINT
);
package com.example.karza.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class KarzaRequest {
    private String pan;
    private String name;
    private String  dob;
    private String consent;


    public KarzaRequest() {}

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getConsent() {
        return consent;
    }

    public void setConsent(String consent) {
        this.consent = consent;
    }

    @Override
    public String toString() {
        return "KarzaRequest{" +
                "pan='" + pan + '\'' +
                ", name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                ", consent='" + consent + '\'' +
                '}';
    }
}
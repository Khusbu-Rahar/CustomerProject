package com.example.model;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;

@Serdeable
public class Customer {

    private int id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String pan;
    private String mobileNumber;
    private String email;
    private String PanStatus;

    public Customer() {
    }

    public Customer(int id, String fullName, LocalDate  dateOfBirth, String pan, String PanStatus, String mobileNumber, String email) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.pan = pan;
        this.PanStatus =  "Pending";
        this.mobileNumber = mobileNumber;
        this.email = email;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate  getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate  dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPanStatus() {
        return PanStatus;
    }

    public void setPanStatus(String panStatus) {
        PanStatus = panStatus;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", pan='" + pan + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", email='" + email + '\'' +
                ", PanStatus='" + PanStatus + '\'' +
                '}';
    }
}
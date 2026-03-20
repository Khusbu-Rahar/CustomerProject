package com.fplabs.model;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fplabs.enums.PanStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Customer {

    @NotNull
    private int id;
    @NotNull
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @NotNull
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String pan;
    @NotNull
    @Pattern(regexp = "^[0-9]{10}$")
    private String mobileNumber;
    @NotNull
    private String email;

    private PanStatus panStatus;

    public Customer() {
    }

    public Customer(int id, String fullName, LocalDate  dateOfBirth, String pan, String PanStatus, String mobileNumber, String email) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.pan = pan;
        this.panStatus =  null;
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

    public PanStatus getPanStatus() {
        return panStatus;
    }

    public void setPanStatus(PanStatus panStatus) {
        this.panStatus = panStatus;
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
                ", panStatus=" + panStatus +
                '}';
    }
}
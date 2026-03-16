package com.example.karza.model;

import com.example.model.CustomerResult;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Result {

    private CustomerResult finalResult;

    private String status;
    private Boolean dobMatch;
    private Boolean duplicate;
    private Boolean nameMatch;

    public CustomerResult getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(CustomerResult finalResult) {
        this.finalResult = finalResult;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDobMatch() {
        return dobMatch;
    }

    public void setDobMatch(Boolean dobMatch) {
        this.dobMatch = dobMatch;
    }

    public Boolean getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        this.duplicate = duplicate;
    }

    public Boolean getNameMatch() {
        return nameMatch;
    }

    public void setNameMatch(Boolean nameMatch) {
        this.nameMatch = nameMatch;
    }
}
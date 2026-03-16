package com.fplabs.model;

public class CustomerResult {

    private String Status;
    private String message;
    private Customer customer;

    public CustomerResult() {
        this.Status = Status;
        this.message = message;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "CustomerResult{" +
                "Status='" + Status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

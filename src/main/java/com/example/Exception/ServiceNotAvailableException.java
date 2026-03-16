package com.example.Exception;

public class ServiceNotAvailableException extends RuntimeException{
    public ServiceNotAvailableException(String message){
        super(message);
    }
}

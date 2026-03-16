package com.example.Exception;

public class DatabaseConnectionException extends RuntimeException{
    public DatabaseConnectionException(String message){
        super(message);
    }
}

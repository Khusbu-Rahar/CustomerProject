package com.example.Exception.handler;

import com.example.Exception.CustomerNotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.util.Map;

@Produces
@Singleton
public class CustomerNotFoundExceptionHandler implements ExceptionHandler<CustomerNotFoundException, HttpResponse> {
    @Override
    public HttpResponse handle(HttpRequest request, CustomerNotFoundException exception) {
        Map<String, Object> errorBody = Map.of(
                "status", HttpStatus.NOT_FOUND.getCode(),
                "error", HttpStatus.NOT_FOUND.getReason(),
                "message", exception.getMessage()
        );
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(errorBody);
    }
}
package com.fplabs.exception.handler;

import com.fplabs.exception.ServiceNotAvailableException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import java.util.Map;

@Produces
@Singleton
public class ServiceNotAvailableExceptionHandler implements ExceptionHandler<ServiceNotAvailableException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, ServiceNotAvailableException exception) {
        Map<String, Object> errorBody = Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.getCode(),
                "error", HttpStatus.SERVICE_UNAVAILABLE.getReason(),
                "message", exception.getMessage()
        );
        return HttpResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorBody);
    }
}
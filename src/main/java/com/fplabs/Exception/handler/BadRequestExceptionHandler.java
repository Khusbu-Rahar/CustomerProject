package com.fplabs.Exception.handler;

import com.fplabs.Exception.BadRequestException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.util.Map;

@Produces
@Singleton
//@Requires(classes = {BadRequestException.class, ExceptionHandler.class})
public class BadRequestExceptionHandler implements ExceptionHandler<BadRequestException, HttpResponse> {
    public HttpResponse handle(HttpRequest request,BadRequestException exception){
        Map<String, Object> errorBody = Map.of(
                "status", HttpStatus.BAD_REQUEST.getCode(),
                "error", HttpStatus.BAD_REQUEST.getReason(),
                "message", exception.getMessage()
        );
        return HttpResponse
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody);
    }
}

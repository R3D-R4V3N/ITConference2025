package com.hogent.ewdj.itconference.advice;

import exceptions.EventNotFoundException;
import exceptions.LokaalNotFoundException;
import exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class ITConferenceErrorAdvice {

    @ResponseBody
    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEventNotFound(EventNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    @ResponseBody
    @ExceptionHandler(LokaalNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleLokaalNotFound(LokaalNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }


    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();
        String parameterName = ex.getName();
        String requiredType = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse("onbekend type");
        String suppliedValue = Optional.ofNullable(ex.getValue()).map(Object::toString).orElse("geen waarde");
        response.put("message", String.format("Ongeldig type voor parameter '%s'. Vereist: %s, Gegeven: '%s'",
                parameterName, requiredType, suppliedValue));
        return response;
    }


    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAllRuntimeExceptions(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Er is een onverwachte fout opgetreden: " + ex.getMessage());
        return response;
    }
}

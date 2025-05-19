// Begin creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/advice/ITConferenceErrorAdvice.java
package com.hogent.ewdj.itconference.advice;

import exceptions.EventNotFoundException;
import exceptions.LokaalNotFoundException;
import exceptions.UserNotFoundException; // Reeds bestaand, maar voor consistentie hier vermeld
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; // Voor type conversie fouten

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice // Geeft aan dat deze klasse uitzonderingen afhandelt voor REST controllers
public class ITConferenceErrorAdvice {

    // Afhandeling voor EventNotFoundException
    @ResponseBody
    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Retourneer HTTP 404 Not Found
    public Map<String, String> handleEventNotFound(EventNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    // Afhandeling voor LokaalNotFoundException
    @ResponseBody
    @ExceptionHandler(LokaalNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Retourneer HTTP 404 Not Found
    public Map<String, String> handleLokaalNotFound(LokaalNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    // Afhandeling voor UserNotFoundException (indien gebruikt in REST context)
    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Retourneer HTTP 404 Not Found
    public Map<String, String> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    // Algemene afhandeling voor IllegalArgumentException (bijv. als een ID null is)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Retourneer HTTP 400 Bad Request
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return response;
    }

    // Afhandeling voor fouten bij het omzetten van request parameters (bijv. String naar Long)
    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Retourneer HTTP 400 Bad Request
    public Map<String, String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();
        String parameterName = ex.getName();
        String requiredType = Optional.ofNullable(ex.getRequiredType()).map(Class::getSimpleName).orElse("onbekend type");
        String suppliedValue = Optional.ofNullable(ex.getValue()).map(Object::toString).orElse("geen waarde");
        response.put("message", String.format("Ongeldig type voor parameter '%s'. Vereist: %s, Gegeven: '%s'",
                parameterName, requiredType, suppliedValue));
        return response;
    }

    // Algemene afhandeling voor andere onverwachte RuntimeExceptions
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Retourneer HTTP 500 Internal Server Error
    public Map<String, String> handleAllRuntimeExceptions(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Er is een onverwachte fout opgetreden: " + ex.getMessage());
        return response;
    }
}
// Einde creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/com/hogent/ewdj/itconference/advice/ITConferenceErrorAdvice.java
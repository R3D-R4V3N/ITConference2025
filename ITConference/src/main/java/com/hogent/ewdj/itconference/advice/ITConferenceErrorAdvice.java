// com/hogent/ewdj/itconference/advice/ITConferenceErrorAdvice.java
package com.hogent.ewdj.itconference.advice;

import exceptions.EventNotFoundException;
import exceptions.LokaalNotFoundException;
import exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice; // Gebruik ControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView; // Importeer ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice // Veranderd naar ControllerAdvice
public class ITConferenceErrorAdvice {

    // Bestaande methoden voor RESTful API's blijven behouden (return Map<String, String>)
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

    // NIEUW: Afhandeling van validatiefouten voor web formulieren
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidationExceptions(MethodArgumentNotValidException ex) {
        ModelAndView mav = new ModelAndView();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        mav.addObject("errors", errors);
        // Je moet hier bepalen naar welke view je wilt teruggaan, afhankelijk van de controller.
        // Dit is een uitdaging met één globale advice voor alle controllers.
        // Een oplossing is om de viewnaam als redirect-attribuut mee te geven,
        // of te vallen naar een generieke foutpagina, of specifieke @ControllerAdvice per controller.
        // Voor nu geven we een algemene foutpagina weer, of je moet de specifieke form-view per controller weten.
        // Laten we aannemen dat we in dit geval naar een algemene error pagina gaan.
        mav.addObject("errorMessage", "Validatiefouten: " + errors.values());
        mav.setViewName("error"); // Zorg dat je een 'error.html' template hebt of een andere generieke foutpagina.
        return mav;
    }


    // NIEUW: Generieke foutafhandeling voor alle andere RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllRuntimeExceptions(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("error"); // Verwijst naar error.html
        mav.addObject("errorMessage", "Er is een onverwachte fout opgetreden: " + ex.getMessage());
        // Optioneel kun je ook de HTTP status code in de model toevoegen voor de view
        mav.addObject("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return mav;
    }

    // Optioneel: Voeg een handler toe voor IllegalStateException, zoals in ewdj2024ep3festivals
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView("redirect:/error"); // Redirect naar een algemene foutpagina
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value()); // Kan ook 400 zijn
        return mav;
    }
}
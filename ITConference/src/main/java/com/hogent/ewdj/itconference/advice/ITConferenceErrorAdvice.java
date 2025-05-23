package com.hogent.ewdj.itconference.advice;

import exceptions.EventNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ITConferenceErrorAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        return "redirect:/error";
    }

    @ExceptionHandler(EventNotFoundException.class)
    public String handleEventNotFoundException(EventNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.NOT_FOUND.value());
        return "redirect:/error";
    }
}
package com.hogent.ewdj.itconference.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ITConferenceErrorAdvice {

    @ExceptionHandler(RuntimeException.class)
    public String handleAllRuntimeExceptions(RuntimeException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "redirect:/error";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorCode", HttpStatus.BAD_REQUEST.value());
        return "redirect:/error";
    }
}
package com.hogent.ewdj.itconference.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import java.util.Locale;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MESSAGE = "errorMessage";

    @Autowired
    private MessageSource messageSource;

    @RequestMapping("/error")
    public String handleError(
            @RequestParam(name = "errorCode", required = false) String errorCodeParam,
            @RequestParam(name = "errorMessage", required = false) String errorMessageParam,
            HttpServletRequest request,
            Model model,
            Locale locale) {

        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        String errorCode = (String) request.getSession().getAttribute("errorCode");

        request.getSession().removeAttribute("errorMessage");
        request.getSession().removeAttribute("errorCode");

        if (errorMessage == null) {
            errorMessage = errorMessageParam;
        }
        if (errorCode == null) {
            errorCode = errorCodeParam;
        }

        if (errorMessage == null && errorCode == null) {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

            if (status != null) {
                Integer statusCode = Integer.valueOf(status.toString());

                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    errorCode = "404";
                    errorMessage = messageSource.getMessage("error.404", null, "Page not found", locale);
                } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                    errorCode = "403";
                    errorMessage = messageSource.getMessage("error.403", null, "Access is denied", locale);
                } else {
                    errorCode = statusCode.toString();
                    errorMessage = messageSource.getMessage("error.unexpected", null, "An unexpected error occurred", locale);
                }
            }
        }

        // Always add attributes, even if null
        model.addAttribute(ERROR_CODE, errorCode);
        model.addAttribute(ERROR_MESSAGE, errorMessage);

        return "error";
    }
}